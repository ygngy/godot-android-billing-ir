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

public class PurchaseListener extends BillingListener {

    private static final String PURCHASE_SUCCEED = "purchase_succeed"; // With a String (sku of purchased item)
    private static final String PURCHASE_FAILED = "purchase_failed";  // with an int (error code) and a string (error msg)

    protected PurchaseListener(@NonNull IrBillingPlugin plugin) {
        super(plugin);
    }

    @Override
    public void register(@NonNull Set<SignalInfo> signals) {
        signals.add(new SignalInfo(PURCHASE_SUCCEED, String.class));
        signals.add(new SignalInfo(PURCHASE_FAILED, Integer.class, String.class));
    }

    public final IabHelper.OnIabPurchaseFinishedListener listener =
            (result, info) -> {
                if (result.isSuccess()) {
                    mPlugin.notifyIrClient(PURCHASE_SUCCEED, info.getSku());

                } else {
                    int errorCode = result.getResponse();
                    String errorMsg = result.getMessage();
                    mPlugin.notifyIrClient(PURCHASE_FAILED, errorCode, errorMsg);
                    Logger.logDebug("purchase failed - error code: " + errorCode + " error msg: " + errorMsg);
                }
            };
}
