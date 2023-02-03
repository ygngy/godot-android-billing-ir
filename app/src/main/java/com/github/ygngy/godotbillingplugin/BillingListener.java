/*
 * Copyright (c) 2023 Mohamadreza Amani
 * Github: https://github.com/ygngy/
 * Linkedin: https://www.linkedin.com/in/ygngy/
 * Email: amany1388@gmail.com
 */

package com.github.ygngy.godotbillingplugin;

import androidx.annotation.NonNull;

import org.godotengine.godot.plugin.SignalInfo;

import java.util.Set;

public abstract class BillingListener {
    @NonNull
    protected final IrBillingPlugin mPlugin;

    protected BillingListener(@NonNull IrBillingPlugin plugin){
        mPlugin = plugin;
    }

    public abstract void register(@NonNull Set<SignalInfo> signals);
}
