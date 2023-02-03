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

public class ConsumeListener extends BillingListener{

    private static final String CONSUME_SUCCEED = "consume_succeed"; // With a String (consumed sku)
    private static final String CONSUME_FAILED = "consume_failed"; // With a String (unconsumed sku)

    protected ConsumeListener(@NonNull IrBillingPlugin plugin) {
        super(plugin);
    }

    @Override
    public void register(@NonNull Set<SignalInfo> signals) {
        signals.add(new SignalInfo(CONSUME_SUCCEED, String.class));
        signals.add(new SignalInfo(CONSUME_FAILED, String.class));
    }

    IabHelper.OnConsumeFinishedListener listener = (purchase, result) -> {
        Logger.logDebug( "Consumption finished. Purchase: " + purchase + ", result: " + result);

        mPlugin.notifyIrClient(result.isSuccess() ? CONSUME_SUCCEED : CONSUME_FAILED, purchase.getSku());

        if (result.isSuccess()) {
            Logger.logDebug( "Consumption successful. Provisioning.");
        } else {
            Logger.logDebug( "Error while consuming: " + result);
        }
        Logger.logDebug( "End consumption flow.");
    };
}
