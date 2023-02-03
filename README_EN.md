# Godot game engine plugins for Myket and CafeBazaar in-app purchases
<br>

## [مشاهده این صفحه به زبان فارسی](README.md)

<br>
  
This plugin provides the possibility to use the in-app purchase of [CafeBazaar](https://cafebazaar.ir) and [Myket](https://myket.ir) stores for the [Godot](https://godotengine.org) game engine.

<br>

## Description


With the help of this plugin, you can add in-app purchase of [Myket](https://myket.ir) and [CafeBazaar](https://cafebazaar.ir) to games made with the [Godot](https://godotengine.org) game engine. For this, just download the zip file in the [Release](https://github.com/ygngy/godot-android-billing-ir/releases) section and copy its content to the **android/plugins** folder. You can read how to use the Android plugin in Godot [here](https://docs.godotengine.org/en/stable/tutorials/plugins/android/android_plugin.html#loading-and-using-an-android-plugin).


<br>
<br>
<br>

## Names of plugins and how to use them



**Note: You cannot use Myket and Cafebazaar plugins at the same time in one apk file.**
**You need to export separate apk files for Myket and Cafebazaar.**
**To do this, every time you export the game, only tick one of these two plugins so that a special apk file is created for it.**

<br>
<br>


## How to use plugins in Godot script

Here is a sample godot script for using this plugin. You may use it in another way as your needs.

```python

var bazaar_plugin_name = "GodotBilling_bazaar" # use this name for cafebazaar
var myket_plugin_name  = "GodotBilling_myket" # use this name for myket

var app_public_key = "get your app public key from cafebazaar and myket" # get app's public key from selected store

var billing = null

func _ready():
	if Engine.has_singleton(myket_plugin_name):
		billing = Engine.get_singleton(myket_plugin_name)
		billing.setApplicationKey(app_public_key)
		billing.setDebugMode(true) # only for debugging and output printing set this to true
		billing.connect("connection_succeed", self, "_on_Connected") # (message)
		billing.connect("connection_failed", self, "_on_ConnectionFailed") # (message)
		billing.connect("query_sku_details_succeed", self, "_on_QuerySucceed") # (dictionaryArray)
		billing.connect("query_sku_details_failed", self, "_on_QueryFailed") # ()
		billing.connect("purchase_succeed", self, "_on_PurchaseSucceed") # (sku)
		billing.connect("purchase_failed", self, "_on_PurchaseFailed") # (errorCode, errorMessage)
		billing.connect("consume_succeed", self, "_on_ConsumeSucceed") # (sku)
		billing.connect("consume_failed", self, "_on_ConsumeFailed") # (sku)
		# As soon as the game is launched, we check the status of the user's purchases
		# If the premium mode has been purchased, we will activate it for the user
		# If the user has a consumable purchase that has not been consumed, we must consume it
		# And after consuming the purchase, we save it in the game
		# The above processes are started by executing the following line
		var requested = billing.startConnection() # If the process starts, the return value is true
	else:
		print("billing plugin not found")



func _reconnect_Button_Pressed():
	# If we could not connect to the store after starting the game
	# Specify a button for "reconnect" and when it is clicked you should execute this function
    # Here you must disable the "Reconnect" button first
	if billing == null:
		print("InAppBilling plugin is not available for this device.")
	else:
		billing.startConnection()

func exit():
	if billing != null:
		billing.endConnection()


func _on_Connected(msg: String):
	print("Successfully connected to billing service")
	billing.querySkuDetails()
		

func _on_ConnectionFailed(msg: String):
	print("Can not connect to billing service")
	# In this case, the connection to the store has failed
	# It is better to inform the user here in a dialog
	# This can be due to not being connected to the Internet or not installing Myket or Bazaar
    # Here you need to enable the "reconnect" button and show it to the user
	
func _on_QuerySucceed(results):
	# Products information is in the parameter of this function
	# The parameter of this function is an array of dictionaries, the keys of each dictionary are as follows
	# "product_id", "type", "price", "title", "description"
	for res in results:
		print(res)	
	# Now we can receive purchases as follows
	var _p = billing.queryPurchases()
	# The output of this function is a dictionary with one key, "purchases" key that has array of purchases.
	# The keys for each purchase are as follows:
	# "order_id", "package_name", "purchase_state", "purchase_time", "signature", "sku"
	# Here, if there is a purchase that is consumable, you must consume it
	var purchases = _p["purchases"]
	for pur in purchases:
		if pur["sku"] == SKU_PREMIUM:
			# Since the premium mode has already been purchased, we activate it
			_setGamePremium()
			return
	# Premium mode has not been purchased before, if it is required, start the process of purchasing it
	billing.purchase(SKU_PREMIUM)
	        


func _on_QueryFailed():
	# Information about the goods and purchases of the user was not received
	# Retrying to receive information
	billing.querySkuDetails()


func _setGamePremium():
	print("Game is premium now.")
    # The game is purchased, you must activate the premium mode for the user
    # For a better experience, somehow inform the user that the game is in premium mode
    # For example, inform the user in the dialogue or toast
	

func _on_PurchaseSucceed(sku: String):
	print("Purchase Succeed SKU: ", sku)
	# Here, use a dialog to display the purchase result
	# If the purchase is not consumable, activate it for the user
	# But if the purchase is consumable, first consume the purchase using the "consume" function
	var requested = billing.consume(sku) # Use the consume function only if the purchase is consumable
    

func _on_PurchaseFailed(errorCode: int, errorMessage: String):
	print("Purchase Failed error code: " + str(errorCode) + " error message: " + errorMessage)



func _on_ConsumeSucceed(sku: String):
    print("Consume Succeed sku: ", sku)
	# This function is executed only when the purchase consumption is successful
	# So here you must save the purchased item in the game
	# because after consuming the purchase, the user no longer owns it in the market and can buy it again

func _on_ConsumeFailed(sku: String):
	print("Consume Failed sku: ", sku)
	# Purchase consumption failed, retry to consume it
	var requested = billing.consume(sku) # If the consumption process starts, the return value is true



```


--------------------------------------------------------------------------


**"Mohamadreza Amani"**  

My LinkedIn Profile: [https://www.linkedin.com/in/ygngy](https://www.linkedin.com/in/ygngy)

My Github Profile: [https://github.com/ygngy](https://github.com/ygngy)  

Email:  [help4usr@gmail.com](mailto:help4usr@gmail.com)

