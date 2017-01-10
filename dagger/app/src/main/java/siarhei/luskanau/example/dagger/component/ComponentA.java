package siarhei.luskanau.example.dagger.component;

import dagger.Component;
import siarhei.luskanau.example.dagger.model.ModelA;
import siarhei.luskanau.example.dagger.module.ModuleA;
import siarhei.luskanau.example.dagger.scope.ScopeA;
import siarhei.luskanau.example.dagger.target.TargetA;

@Component(modules = {ModuleA.class})
@ScopeA
public interface ComponentA {

    void inject(TargetA targetA);

    ModelA modelA();
}