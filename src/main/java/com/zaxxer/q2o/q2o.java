package com.zaxxer.q2o;

import javax.sql.DataSource;
import javax.transaction.TransactionManager;
import javax.transaction.UserTransaction;

import com.zaxxer.q2o.transaction.TxTransactionManager;

/** Single point of q2o configuration */
public final class q2o {

   private q2o() {
   }

   /**
    * Use this one if you don't need {@link TransactionManager} tx handling.
    *
    * @param dataSource the {@link DataSource} to use by the default
    * @return dataSource that will be used for queries
    */
   public static DataSource initializeTxNone(DataSource dataSource) {
      SqlClosure.setDefaultDataSource(dataSource);
      return dataSource;
   }

   /**
    * Use this one to use simple embedded {@link TransactionManager} implementation for tx handling.
    *
    * @param dataSource the {@link DataSource} to use by the default
    * @return dataSource that will be used for queries
    */
   public static DataSource initializeTxSimple(DataSource dataSource) {
      TxTransactionManager txManager = new TxTransactionManager(dataSource);
      return initializeTxCustom(txManager.getTxDataSource(), txManager, txManager);
   }

   /**
    * Use this one if you have custom/provided {@link TransactionManager}, e.g. to run within web app container.
    *
    * @param dataSource the {@link DataSource} to use by the default
    * @param txManager the {@link TransactionManager} to use for tx management
    * @param userTx the {@link UserTransaction} to use for tx management together with txManager
    * @return dataSource that will be used for queries
    */
   public static DataSource initializeTxCustom(DataSource dataSource, TransactionManager txManager, UserTransaction userTx) {
      TransactionHelper.setTransactionManager(txManager);
      TransactionHelper.setUserTransaction(userTx);
      return initializeTxNone(dataSource);
   }

   /**
    * You can reset q2o to a fresh state if desired.
    * E.g. if you want to call another initializeXXX method.
    */
   public static void deinitialize() {
      SqlClosure.setDefaultDataSource(null);
      TransactionHelper.setUserTransaction(null);
      TransactionHelper.setTransactionManager(null);
   }
}
