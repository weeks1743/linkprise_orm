package com.linkprise.orm.mapping;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class DefaultDbNameGuesser implements IDbNameGuesser {
	public Collection<String> getPossibleColumnNames(Method member) {
		if (ClassUtil.isGetter(member)) {
			if (member.getName().startsWith("is")) {
				return getPossibleNames(member.getName().substring(2,
						member.getName().length()));
			}
			return getPossibleNames(member.getName().substring(3,
					member.getName().length()));
		}
		if (ClassUtil.isSetter(member)) {
			return getPossibleNames(member.getName().substring(3,
					member.getName().length()));
		}
		return getPossibleNames(member.getName());
	}

	public Collection<String> getPossibleNames(String fieldname) {
		Set names = new HashSet();
		String fieldName = fieldname;
		String fieldNameFirstLetterLowerCase = fieldName.substring(0, 1)
				.toLowerCase() + fieldName.substring(1, fieldName.length());
		String fieldNameFirstLetterUpperCase = fieldName.substring(0, 1)
				.toUpperCase() + fieldName.substring(1, fieldName.length());
		names.add(fieldName);
		names.add(fieldNameFirstLetterUpperCase);
		names.add(fieldNameFirstLetterLowerCase);
		names.add(fieldName.toUpperCase());
		names.add(fieldName.toLowerCase());

		List words = getWords(fieldName);

		String underScoreFieldName = getUnderScoreSeparatedWordsColumnName(words);
		names.add(underScoreFieldName);
		names.add(underScoreFieldName.toLowerCase());
		names.add(underScoreFieldName.toUpperCase());
		String numUnderScoreFieldName = getNumberUnderScoreSeparatedWordsColumnName(words);
		names.add(numUnderScoreFieldName);
		names.add(numUnderScoreFieldName.toLowerCase());
		names.add(numUnderScoreFieldName.toUpperCase());

		String possColumnName = getPossibleColumnName(fieldName);
		names.add(possColumnName);
		names.add(possColumnName.toLowerCase());
		names.add(possColumnName.toUpperCase());
		String possNumColumnName = getNumberPossibleColumnName(fieldName);
		names.add(possNumColumnName);
		names.add(possNumColumnName.toLowerCase());
		names.add(possNumColumnName.toUpperCase());
		return names;
	}

	private String getPossibleColumnName(String fieldName) {
		StringBuffer name = new StringBuffer();
		char[] chars = fieldName.toCharArray();
		for (char c : chars) {
			if ((Character.isUpperCase(c)) && (name.length() > 0)) {
				name.append('_');
			}
			name.append(c);
		}
		return name.toString();
	}

	private String getNumberPossibleColumnName(String fieldName) {
		StringBuffer name = new StringBuffer();
		char[] chars = fieldName.toCharArray();
		for (char c : chars) {
			if (((Character.isDigit(c)) || (Character.isUpperCase(c)))
					&& (name.length() > 0)) {
				name.append('_');
			}
			name.append(c);
		}
		return name.toString();
	}

	private String getUnderScoreSeparatedWordsColumnName(List<String> words) {
		StringBuffer buffer = new StringBuffer();
		int i = 1;
		for (String word : words) {
			buffer.append(word);
			if (i < words.size()) {
				buffer.append('_');
			}
			i++;
		}

		return buffer.toString();
	}

	private String getNumberUnderScoreSeparatedWordsColumnName(
			List<String> words) {
		String buffer = "";
		int i = 1;
		for (String word : words) {
			if (isNumeric(word)) {
				buffer = buffer.substring(0, buffer.length() - 1);
			}
			buffer = buffer + word;
			if (i < words.size()) {
				buffer = buffer + '_';
			}
			i++;
		}

		return buffer;
	}

	public static boolean isNumeric(String str) {
		int i = str.length();
		do {
			if (!Character.isDigit(str.charAt(i)))
				return false;
			i--;
		} while (i >= 0);

		return true;
	}

	protected List<String> getWords(String fieldName) {
		WordTokenizer tokenizer = new WordTokenizer(fieldName);
		List words = new ArrayList();

		while (tokenizer.hasMoreWords()) {
			words.add(tokenizer.nextWord());
		}

		return words;
	}

	public Collection<String> getPossibleTableNames(Class<?> object) {
		Collection possibleNames = getPossibleNames(ClassUtil
				.classNameWithoutPackage(object));
		return possibleNames;
	}
}