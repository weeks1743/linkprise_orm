package com.linkprise.orm.convert;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

public class GenericConverterFactory {
	private static final GenericConverterFactory _INSTANCE = new GenericConverterFactory();
	private final List<IGenericConverter> _converters;
	private final List<IReverseGenericConverter> _reverseDiscoveryConverters;
	private final Map<Key, ITypeConverter> _cache;
	private static final ITypeConverter _NULL = new ITypeConverter() {
		public Object convert(Object source, Class<?> targetType) {
			return null;
		}
	};

	private GenericConverterFactory() {
		this._cache = new ConcurrentHashMap(16);
		this._converters = new ArrayList(2);
		registerConverter(new SqlConverter());
		registerConverter(new BaseConverter());
		this._reverseDiscoveryConverters = new ArrayList(1);
		registerReverseDiscoveryConverter(new ReflectionReverseGenericConverter());
	}

	public static GenericConverterFactory getInstance() {
		return _INSTANCE;
	}

	public IGenericConverter getSqlConverter() {
		return (IGenericConverter) this._converters.get(0);
	}

	public void registerConverter(IGenericConverter converter) {
		this._converters.add(converter);
		this._cache.clear();
	}

	public void registerReverseDiscoveryConverter(
			IReverseGenericConverter converter) {
		this._reverseDiscoveryConverters.add(converter);
		this._cache.clear();
	}

	public boolean needConvert(Class<?> sourceType, Class<?> targetType) {
		return !targetType.isAssignableFrom(sourceType);
	}

	public ITypeConverter getConverter(Class<?> sourceType, Class<?> targetType) {
		Key key = new Key(sourceType, targetType);

		Object cached = this._cache.get(key);
		if (cached != null) {
			return cached == _NULL ? null : (ITypeConverter) cached;
		}

		Node start = new Node(null, null, sourceType);
		LinkedList sourcesToBeSearched = new LinkedList();
		sourcesToBeSearched.add(start);

		Set cache = new HashSet(16);

		ITypeConverter converter = _findConverter(sourcesToBeSearched,
				targetType, cache, false);

		if ((converter == null)
				&& (this._reverseDiscoveryConverters.size() > 0)) {
			ITypeConverter reverseConv = null;
			reverseConv = (ITypeConverter) this._cache.get(new Key(targetType,
					sourceType));
			if (reverseConv == null) {
				cache.clear();
				sourcesToBeSearched.add(new Node(null, null, targetType));
				reverseConv = _findConverter(sourcesToBeSearched, sourceType,
						cache, false);
			}

			if (reverseConv != null) {
				cache.clear();
				sourcesToBeSearched.clear();
				sourcesToBeSearched.add(start);
				converter = _findConverter(sourcesToBeSearched, targetType,
						cache, true);
			}
		}

		if (converter == null) {
			this._cache.put(key, _NULL);
		} else
			this._cache.put(key, converter);

		return converter;
	}

	private ITypeConverter _findConverter(LinkedList<Node> sourcesToBeSearched,
			Class<?> targetType, Set<Class<?>> cache,
			boolean useRevserseDiscovery) {
		while (!sourcesToBeSearched.isEmpty()) {
			Node source = (Node) sourcesToBeSearched.removeFirst();
			ITypeConverter match = null;

			for (IGenericConverter conv : this._converters) {
				if (_searchTargetTypes(sourcesToBeSearched, source, conv,
						targetType, cache)) {
					match = conv;
				}
			}

			if ((match == null) && (useRevserseDiscovery)) {
				match = _searchSourceTypes(source.targetType, targetType);
			}
			if (match == null)
				continue;
			if (source.previous == null) {
				return match;
			}

			return new CompositeConverter(source, match, targetType);
		}

		return null;
	}

	private boolean _searchTargetTypes(List<Node> sourcesToBeSearched,
			Node currentSource, IGenericConverter currentConverter,
			Class<?> searchType, Set<Class<?>> cache) {
		Class sourceType = currentSource.targetType;
		List targetTypes = currentConverter.getTargetTypes(sourceType);
		int i = 0;
		for (int sz = targetTypes.size(); i < sz; i++) {
			Class targetType = (Class) targetTypes.get(i);

			if (!cache.add(targetType)) {
				continue;
			}
			if (searchType.isAssignableFrom(targetType)) {
				return true;
			}

			Node newSource = new Node(currentSource, currentConverter,
					targetType);

			sourcesToBeSearched.add(newSource);
		}

		return false;
	}

	private ITypeConverter _searchSourceTypes(Class<?> sourceType,
			Class<?> targetType) {
		for (IReverseGenericConverter conv : this._reverseDiscoveryConverters) {
			List<Class<?>> sourceTypes = conv.getSourceTypes(targetType);
			for (Class type : sourceTypes) {
				if (type.isAssignableFrom(sourceType))
					return conv;
			}
		}
		return null;
	}

	private static final class CompositeConverter implements ITypeConverter {
		private final GenericConverterFactory.Node _chain;

		public CompositeConverter(GenericConverterFactory.Node source,
				ITypeConverter conv, Class<?> targetType) {
			assert (source != null);
			this._chain = new GenericConverterFactory.Node(source, conv,
					targetType);
		}

		public Object convert(Object source, Class<?> targetType)
				throws TypeConvertException {
			if (targetType.isAssignableFrom(this._chain.targetType)) {
				return this._chain.convert(source);
			}
			throw new IllegalArgumentException("CANNOT_CONVERT");
		}
	}

	private static final class Key {
		private final int _hc;
		private final Class<?> _source;
		private final Class<?> _target;

		public Key(Class<?> source, Class<?> target) {
			assert (!source.equals(target));
			this._source = source;
			this._target = target;
			this._hc = (source.hashCode() + target.hashCode());
		}

		public int hashCode() {
			return this._hc;
		}

		public boolean equals(Object other) {
			if (this == other)
				return true;
			if ((other instanceof Key)) {
				Key that = (Key) other;
				return (this._source.equals(that._source))
						&& (this._target.equals(that._target));
			}
			return false;
		}
	}

	private static final class Node {
		public final Node previous;
		public final ITypeConverter converter;
		public final Class<?> targetType;

		public Node(Node previous, ITypeConverter converter, Class<?> targetType) {
			this.previous = previous;
			this.converter = converter;
			this.targetType = targetType;
		}

		public Object convert(Object source) throws TypeConvertException {
			if (this.previous != null) {
				source = this.previous.convert(source);
				source = this.converter.convert(source, this.targetType);
			}
			return source;
		}
	}
}