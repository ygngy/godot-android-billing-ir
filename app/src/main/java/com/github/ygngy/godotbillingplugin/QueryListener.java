/*
 * Copyright (c) 2023 Mohamadreza Amani
 * Github: https://github.com/ygngy/
 * Linkedin: https://www.linkedin.com/in/ygngy/
 * Email: amany1388@gmail.com
 */

package com.github.ygngy.godotbillingplugin;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import org.godotengine.godot.Dictionary;
import org.godotengine.godot.plugin.SignalInfo;

import java.util.List;
import java.util.Set;
import ir.myket.billingclient.IabHelper;
import ir.myket.billingclient.util.Inventory;
import ir.myket.billingclient.util.Purchase;
import ir.myket.billingclient.util.SkuDetails;

public class QueryListener extends BillingListener {
    private static final String QUERY_SUCCEED = "query_sku_details_succeed"; // With an Array of Dictionary
    private static final String QUERY_FAILED = "query_sku_details_failed";

    @Nullable
    private Inventory mInventory = null;
    public boolean hasInventory(){
        return mInventory != null;
    }

    @Nullable
    public List<Purchase> getAllPurchases(){
        return mInventory != null ? mInventory.getAllPurchases() : null;
    }

    public QueryListener(@NonNull IrBillingPlugin plugin) {
        super(plugin);
    }

    @Override
    public void register(@NonNull Set<SignalInfo> signals) {
        signals.add(new SignalInfo(QUERY_SUCCEED, Object[].class));
        signals.add(new SignalInfo(QUERY_FAILED));
    }

    @NonNull
    public final IabHelper.QueryInventoryFinishedListener listener =
            (result, inv) -> {
                Logger.logInfo("Query finished result: " + result.isSuccess());
                if (result.isSuccess() && inv != null) {
                    mInventory = inv;

                    Logger.logDebug("======= Purchases ======");
                    for (Purchase item: inv.getAllPurchases()) {
                        Logger.logDebug( "SKU: " + item.getSku() + " Order id: " + item.getOrderId());
                    }
                    Logger.logDebug("======= Products =======");
                    for (SkuDetails item: inv.getAllProducts()) {
                        Logger.logDebug("SKU: " + item.getSku() + " Price: " + item.getPrice());
                    }

                    Object[] allProducts = new Object[inv.getAllProducts().size()];
                    int i = 0;
                    for (SkuDetails sku : inv.getAllProducts()) {
                        Dictionary currProduct = new Dictionary();

                        currProduct.set_keys(new String[]{
                                "product_id", "type", "price", "title", "description"}
                        );
                        currProduct.set_values(new String[]{
                                sku.getSku(), sku.getType(), sku.getPrice(), sku.getTitle(),
                                sku.getDescription()
                        });
                        allProducts[i] = currProduct;
                        ++i;
                    }
                    mPlugin.notifyIrClient(QUERY_SUCCEED, (Object)allProducts);

                } else {
                    mPlugin.notifyIrClient(QUERY_FAILED);
                }
            };


    @Nullable
    public Dictionary queryPurchases() {
        List<Purchase> allPurchases = getAllPurchases();
        if (allPurchases == null) {
            return null;
        }

        Object[] purchasesArray = new Object[allPurchases.size()];
        int i = 0;
        for (Purchase pur: allPurchases) {
            Dictionary dictionary = new Dictionary();
            dictionary.put("order_id", pur.getOrderId());
            dictionary.put("package_name", pur.getPackageName());
            dictionary.put("purchase_state", pur.getPurchaseState());
            dictionary.put("purchase_time", pur.getPurchaseTime());
            dictionary.put("signature", pur.getSignature());
            dictionary.put("sku", pur.getSku());

            purchasesArray[i] = dictionary;
            ++i;
        }

        Dictionary purchases = new Dictionary();
        purchases.put("purchases", purchasesArray);

        return purchases;
    }
}
