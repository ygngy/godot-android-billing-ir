/*
 * Copyright (c) 2023 Mohamadreza Amani
 * Github: https://github.com/ygngy/
 * Linkedin: https://www.linkedin.com/in/ygngy/
 * Email: amany1388@gmail.com
 */

package com.github.ygngy.godotbillingplugin;

import android.content.Context;
import androidx.annotation.NonNull;
import org.godotengine.godot.Dictionary;
import org.godotengine.godot.Godot;
import org.godotengine.godot.plugin.GodotPlugin;
import org.godotengine.godot.plugin.SignalInfo;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import ir.myket.billingclient.IabHelper;
import ir.myket.billingclient.util.Purchase;


public class IrBillingPlugin extends GodotPlugin {
    private final Godot mGodot;
    private IabHelper mHelper;

    @NonNull
    private final SetupListener mSetupListener = new SetupListener(this);
    @NonNull
    private final QueryListener mQueryListener = new QueryListener(this);
    @NonNull
    private final PurchaseListener mPurchaseListener = new PurchaseListener(this);
    @NonNull
    private final ConsumeListener mConsumeListener = new ConsumeListener(this);
    private String mBase64Key = "";
    public void setApplicationKey(String key) {
        mBase64Key = key;
    }

    public IrBillingPlugin(Godot godot) {
        super(godot);
        mGodot = godot;
    }

    public void notifyIrClient(String signalName, Object... signalArgs){
        try {
            emitSignal(signalName, signalArgs);
        }catch (Exception e) {
            Logger.logError( "Emit failed: " + e.getMessage());
        }
    }

    @NonNull
    @Override
    public String getPluginName() {
        return BuildConfig.GODOT_PLUGIN_NAME;
    }

    @NonNull
    @Override
    public List<String> getPluginMethods() {
        return Arrays.asList("startConnection", "endConnection", "querySkuDetails",
                "setApplicationKey", "purchase", "queryPurchases",
                "consume", "setDebugMode");
    }

    public void setDebugMode(boolean debugging){
        Logger.isDebugging = debugging;
    }

    @NonNull
    @Override
    public Set<SignalInfo> getPluginSignals() {
        Set<SignalInfo> signals = new HashSet<>();
        mSetupListener.register(signals);
        mQueryListener.register(signals);
        mPurchaseListener.register(signals);
        mConsumeListener.register(signals);
        return signals;
    }


    public boolean startConnection() {
        Logger.logInfo("starting connection.");

        if (mBase64Key == null || "".equals(mBase64Key.trim())) {
            Logger.logError("Set Application key first with \"setApplicationKey\" function");
            return false;
        }

        final Context context = mGodot.getContext();

        if (context == null){
            Logger.logError("error: Null context in Godot");
            return false;
        }

        mHelper = new IabHelper(context, mBase64Key);
        try {
            mHelper.startSetup(mSetupListener.listener);
        } catch (Exception e) {
            Logger.logError( "Start Connection: " + e.getMessage());
            return false;
        }
        return true;
    }

    public void endConnection() {
        mHelper.dispose();
    }

    public void querySkuDetails() {
        try {
            mGodot.runOnUiThread(() -> mHelper.queryInventoryAsync(mQueryListener.listener));
        } catch (Exception e) {
            Logger.logError("Query: " + e.getMessage());
        }
    }

    public Dictionary queryPurchases() {
        return mQueryListener.queryPurchases();
    }

    public void purchase(String productId) {
        mHelper.launchPurchaseFlow(mGodot.getActivity(), productId, mPurchaseListener.listener);
    }

    public boolean consume(String sku) {
        if (!mQueryListener.hasInventory()){
            Logger.logDebug( "cant consume: Inventory not fetched");
            return false;
        }
        List<Purchase> purchases = mQueryListener.getAllPurchases();
        if (purchases != null)
            for (Purchase purchase: purchases) {
                if (purchase.getSku().equals(sku)) {
                    mGodot.runOnUiThread(() -> {
                        // if we were disposed of in the meantime, quit.
                        if (mHelper != null)
                            mHelper.consumeAsync(purchase, mConsumeListener.listener);
                    });
                    return true;
                }
            }
        return false;
    }

}
