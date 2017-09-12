package io.totokaka.strikk.example.dagger;

import dagger.BindsInstance;
import io.totokaka.strikk.example.StrikkExample;

import javax.inject.Singleton;

@Singleton
@dagger.Component(modules = {Module.class})
public interface Component {

    void inject(StrikkExample strikkExample);

    @dagger.Component.Builder
    interface Builder {

        @BindsInstance
        Builder bind(StrikkExample strikkExample);

        Component build();
    }

}
