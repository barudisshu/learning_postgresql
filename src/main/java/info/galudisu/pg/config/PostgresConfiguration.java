package info.galudisu.pg.config;

import org.jooq.impl.DataSourceConnectionProvider;
import org.jooq.impl.DefaultConfiguration;
import org.jooq.impl.DefaultDSLContext;
import org.jooq.impl.DefaultExecuteListenerProvider;
import org.springframework.boot.autoconfigure.jooq.JooqExceptionTranslator;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.datasource.TransactionAwareDataSourceProxy;

import javax.sql.DataSource;

@Configuration
public class PostgresConfiguration {

  @Bean
  public DataSourceConnectionProvider connectionProvider(DataSource dataSource) {
    return new DataSourceConnectionProvider(new TransactionAwareDataSourceProxy(dataSource));
  }

  @Bean
  public DefaultConfiguration configuration(DataSourceConnectionProvider connectionProvider) {
    DefaultConfiguration jooqConfiguration = new DefaultConfiguration();
    jooqConfiguration.set(connectionProvider);
    jooqConfiguration.set(new DefaultExecuteListenerProvider(new JooqExceptionTranslator()));

    return jooqConfiguration;
  }

  @Bean
  public DefaultDSLContext dsl(DefaultConfiguration configuration) {
    return new DefaultDSLContext(configuration);
  }
}
