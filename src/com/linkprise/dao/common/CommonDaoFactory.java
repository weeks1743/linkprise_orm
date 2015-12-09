package com.linkprise.dao.common;

public class CommonDaoFactory {
	public static ICommonDao createCommonDao(DatasourceConfig config) {
		ConnectionFactory connectionFactory = new DefaultConnectionFactory(
				config);
		ICommonDao commonDao = new CommonDaoImpl(connectionFactory);
		return commonDao;
	}

	public static ICommonDaoXmlExt createCommonDaoXmlExt(DatasourceConfig config) {
		ConnectionFactory connectionFactory = new DefaultConnectionFactory(
				config);
		ICommonDaoXmlExt commonDao = new CommonDaoXmlExtImpl(connectionFactory);
		return commonDao;
	}

	public static ICommonDao createOneThreadMultiCommonDao(
			DatasourceConfig config) {
		ConnectionFactory connectionFactory = new DefaultConnectionFactory(
				config);
		ICommonDao commonDao = new OneThreadMultiConnectionCommonDaoImpl(
				connectionFactory);
		return commonDao;
	}

	public static ICommonDao createCommonDao(DatasourceConfig config,
			boolean autoManagerTransaction) {
		ConnectionFactory connectionFactory = new DefaultConnectionFactory(
				config);
		ICommonDao commonDao = new CommonDaoImpl(connectionFactory);
		commonDao.setAutoManagerTransaction(autoManagerTransaction);
		return commonDao;
	}

	public static ICommonDaoXmlExt createCommonDaoXmlExt(
			DatasourceConfig config, boolean autoManagerTransaction) {
		ConnectionFactory connectionFactory = new DefaultConnectionFactory(
				config);
		ICommonDaoXmlExt commonDao = new CommonDaoXmlExtImpl(connectionFactory);
		commonDao.setAutoManagerTransaction(autoManagerTransaction);
		return commonDao;
	}

	public static ICommonDao createOneThreadMultiCommonDao(
			DatasourceConfig config, boolean autoManagerTransaction) {
		ConnectionFactory connectionFactory = new DefaultConnectionFactory(
				config);
		ICommonDao commonDao = new OneThreadMultiConnectionCommonDaoImpl(
				connectionFactory);
		commonDao.setAutoManagerTransaction(autoManagerTransaction);
		return commonDao;
	}

	public static ICommonDao createCommonDao(ConnectionFactory connectionFactory) {
		ICommonDao commonDao = new CommonDaoImpl(connectionFactory);
		return commonDao;
	}

	public static ICommonDaoXmlExt createCommonXmlExtDao(
			ConnectionFactory connectionFactory) {
		ICommonDaoXmlExt commonDao = new CommonDaoXmlExtImpl(connectionFactory);
		return commonDao;
	}

	public static ICommonDao createOneThreadMultiCommonDao(
			ConnectionFactory connectionFactory) {
		ICommonDao commonDao = new OneThreadMultiConnectionCommonDaoImpl(
				connectionFactory);
		return commonDao;
	}

	public static ICommonDao createCommonDao(
			ConnectionFactory connectionFactory, boolean autoManagerTransaction) {
		ICommonDao commonDao = new CommonDaoImpl(connectionFactory);
		commonDao.setAutoManagerTransaction(autoManagerTransaction);
		return commonDao;
	}

	public static ICommonDaoXmlExt createCommonDaoXmlExt(
			ConnectionFactory connectionFactory, boolean autoManagerTransaction) {
		ICommonDaoXmlExt commonDao = new CommonDaoXmlExtImpl(connectionFactory);
		commonDao.setAutoManagerTransaction(autoManagerTransaction);
		return commonDao;
	}

	public static ICommonDao createOneThreadMultiCommonDao(
			ConnectionFactory connectionFactory, boolean autoManagerTransaction) {
		ICommonDao commonDao = new OneThreadMultiConnectionCommonDaoImpl(
				connectionFactory);
		commonDao.setAutoManagerTransaction(autoManagerTransaction);
		return commonDao;
	}
}