package com.trifork;

import org.springframework.aop.framework.ProxyFactoryBean;
import org.springframework.aop.target.CommonsPool2TargetSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Scope;

@SpringBootApplication
@Configuration
public class PoolingTargetSourcesApplication implements CommandLineRunner {

	@Autowired
	ApplicationContext applicationContext;

	public static void main(String[] args) {
		SpringApplication.run(PoolingTargetSourcesApplication.class, args);
	}

	@Override
	public void run(String... args) throws Exception {

		Foo fo1 = applicationContext.getBean("foo", Foo.class);

		Foo fo2 = applicationContext.getBean("foo", Foo.class);

		Foo fo3 = applicationContext.getBean("foo", Foo.class);

		System.out.println("Put breakpoint here");
	}


	static class Foo {
	}


	@Bean
	@Scope(BeanDefinition.SCOPE_PROTOTYPE)
	public Foo fooTarget() {
		return new Foo();
	}

	@Bean
	public CommonsPool2TargetSource poolingTargetSource() {
		CommonsPool2TargetSource pooledObjectFactory = new CommonsPool2TargetSource() {
			@Override
			public Object getTarget() throws Exception {
				logger.info("borrow object from pool");
				return super.getTarget();
			}
		};
		pooledObjectFactory.setTargetBeanName("fooTarget");
		return pooledObjectFactory;
	}

	@Bean
	public ProxyFactoryBean foo() {
		ProxyFactoryBean proxyFactoryBean = new ProxyFactoryBean();
		proxyFactoryBean.setTargetSource(poolingTargetSource());
		return proxyFactoryBean;
	}

}
