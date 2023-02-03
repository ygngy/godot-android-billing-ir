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
import ir.myket.billingclient.IabHelper;

public class SetupListener extends BillingListener{

    private static final String CONNECTION_SUCCEED = "connection_succeed"; // with msg String
    private static final String CONNECTION_FAILED = "connection_failed"; // with msg String

    public SetupListener(@NonNull IrBillingPlugin plugin){
        super(plugin);
    }

    @Override
    public void register(@NonNull Set<SignalInfo> signals) {
        signals.add(new SignalInfo(CONNECTION_SUCCEED, String.class));
        signals.add(new SignalInfo(CONNECTION_FAILED, String.class));
    }

    @NonNull
    public final IabHelper.OnIabSetupFinishedListener listener = result -> {
        if (result.isSuccess())
            mPlugin.notifyIrClient(CONNECTION_SUCCEED, result.getMessage());
        else
            mPlugin.notifyIrClient(CONNECTION_FAILED, result.getMessage());
    };
}
