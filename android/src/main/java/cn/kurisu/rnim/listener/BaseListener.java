package cn.kurisu.txim.listener;

import cn.kurisu.txim.module.BaseModule;

public abstract class BaseListener {

    protected BaseModule module;

    public BaseListener(BaseModule module) {
        if (this.module == null)
            this.module = module;
    }
}
