package org.opendaylight.controller.config.yang.config.ping_provider.impl;

import org.opendaylight.controller.ping.plugin.internal.PingImpl;
import org.opendaylight.controller.sal.binding.api.BindingAwareBroker;
import org.opendaylight.yang.gen.v1.urn.opendaylight.ping.rev130911.PingService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PingProviderModule extends org.opendaylight.controller.config.yang.config.ping_provider.impl.AbstractPingProviderModule {

    private static final Logger log = LoggerFactory.getLogger(PingProviderModule.class);

    public PingProviderModule(org.opendaylight.controller.config.api.ModuleIdentifier identifier, org.opendaylight.controller.config.api.DependencyResolver dependencyResolver) {
        super(identifier, dependencyResolver);
    }

    public PingProviderModule(org.opendaylight.controller.config.api.ModuleIdentifier identifier, org.opendaylight.controller.config.api.DependencyResolver dependencyResolver, org.opendaylight.controller.config.yang.config.ping_provider.impl.PingProviderModule oldModule, java.lang.AutoCloseable oldInstance) {
        super(identifier, dependencyResolver, oldModule, oldInstance);
    }

    @Override
    public void customValidation() {
        // add custom validation form module attributes here.
    }

    @Override
    public java.lang.AutoCloseable createInstance() {
        final PingImpl opendaylightPing = new PingImpl();

        // Register to md-sal
        final BindingAwareBroker.RpcRegistration<PingService> rpcRegistration = getRpcRegistryDependency()
                .addRpcImplementation(PingService.class, opendaylightPing);

        // Wrap toaster as AutoCloseable and close registrations to md-sal at
        // close()
        final class AutoCloseableToaster implements AutoCloseable {

            @Override
            public void close() throws Exception {
                rpcRegistration.close();
                log.info("Ping provider (instance {}) torn down.", this);
            }

            private void closeQuietly(final AutoCloseable resource) {
                try {
                    resource.close();
                } catch (final Exception e) {
                    log.debug("Ignoring exception while closing {}", resource, e);
                }
            }
        }

        AutoCloseable ret = new AutoCloseableToaster();
        log.info("Ping provider (instance {}) initialized.", ret);
        return ret;
    }
}
