package free.servpp.cbodjava.config;

import free.cobol2java.java.ServiceManager;
import free.servpp.sppframe.common.IServiceContainer;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Component;

@Component
@Primary
public class BootServiceContainer implements IServiceContainer, ApplicationContextAware {
    private ApplicationContext applicationContext;

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) {
        this.applicationContext = applicationContext;
        ServiceManager.setServiceContainer(this);
    }

    @Override
    public <T> T getService(Class<T> serviceClass) {
        return applicationContext.getBean(serviceClass);
    }

    @Override
    public Object getService(String serviceName) {
        return applicationContext.getBean(serviceName);
    }

    @Override
    public String getServiceName(Class<?> clazz) {
        String[] beanNames = applicationContext.getBeanNamesForType(clazz);
        if (beanNames.length > 0) {
            return beanNames[0];
        }
        return clazz.getSimpleName();
    }
}
