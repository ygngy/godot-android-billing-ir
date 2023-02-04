# Godot game engine plugins for Myket and Cafe Bazaar in-app purchases

<br>

## [View this page in English](README_EN.md)


## پلاگین موتور بازی‌سازی گودو برای پرداخت درون برنامه‌ای کافه‌بازار و مایکت 

<br>

<div dir="rtl">
  
این پلاگین امکان استفاده از پرداخت درون برنامه‌ای  [مایکت](https://myket.ir) و [کافه بازار](https://cafebazaar.ir) را برای موتور بازی سازی [گودو](https://godotengine.org) فراهم میکند.

<br>

## توضیحات

به کمک این پلاگین می توانید پرداخت درون برنامه ای [مایکت](https://myket.ir) و [کافه بازار](https://cafebazaar.ir) را به بازی‌های ساخته شده با موتور بازی سازی [گودو](https://godotengine.org) اضافه کنید. برای این کار کافی است فایل zip موجود در قسمت [*Release*](https://github.com/ygngy/godot-android-billing-ir/releases) را دانلود کرده و محتوای آن را در پوشه **android/plugins** کپی کنید. نحوه استفاده از پلاگین اندروید در گودو را می توانید از [اینجا](https://docs.godotengine.org/en/stable/tutorials/plugins/android/android_plugin.html#loading-and-using-an-android-plugin) بخوانید.



<br>
<br>
<br>

## نام پلاگین‌ها و نحوه‌ی استفاده از آن‌ها



**توجه: پلاگین‌های مایکت و کافه‌بازار را نمی‌توانید به صورت همزمان در یک فایل apk استفاده کنید.**
**باید برای مایکت و کافه‌بازار فایل‌های apk جداگانه export کنید.**
**برای اینکار در هربار اکسپورت کردن بازی فقط تیک یکی از این دو پلاگین را بزنید تا فایل apk مخصوص آن ایجاد شود.**

<br>
<br>


## نحوه استفاده از پلاگین‌ها در اسکریپت گودو 

در اینجا یک نمونه از اسکریپت گودو برای استفاده از این افزونه آورده شده است. شما می توانید آن را به روش دیگری مطابق با نیاز خود استفاده کنید.

</div>


```python

var bazaar_plugin_name = "GodotBilling_bazaar" # برای بازار از این نام استفاده کنید
var myket_plugin_name  = "GodotBilling_myket" # برای مایکت از این نام استفاده کنید

var app_public_key = "get your app public key from cafebazaar and myket" # کلید عمومی برنامه را از بازار و مایکت بگیرید

var billing = null

func _ready():
	if Engine.has_singleton(myket_plugin_name):
		billing = Engine.get_singleton(myket_plugin_name)
		billing.setApplicationKey(app_public_key)
		billing.setDebugMode(true) # درصورت نیاز به مشاهده خروجی و اشکال زدایی آن را مقدار دهی کنید
		billing.connect("connection_succeed", self, "_on_Connected") # (message)
		billing.connect("connection_failed", self, "_on_ConnectionFailed") # (message)
		billing.connect("query_sku_details_succeed", self, "_on_QuerySucceed") # (dictionaryArray)
		billing.connect("query_sku_details_failed", self, "_on_QueryFailed") # ()
		billing.connect("purchase_succeed", self, "_on_PurchaseSucceed") # (sku)
		billing.connect("purchase_failed", self, "_on_PurchaseFailed") # (errorCode, errorMessage)
		billing.connect("consume_succeed", self, "_on_ConsumeSucceed") # (sku)
		billing.connect("consume_failed", self, "_on_ConsumeFailed") # (sku)
		# به محض اجرای اولیه بازی وضعیت خریدهای کاربر را بررسی می‌کنیم
		# اگر حالت پرمیوم خریده شده باشد آن را برای کاربر فعال می‌کنیم	
		# اگر کاربر خرید مصرف شدنی داشته باشد که مصرف نشده است آن را مصرف می‌کنیم
		# و پس از مصرف خرید، آن را در بازی ذخیره می‌کنیم
		# با اجرای خط زیر فرایندهای فوق آغاز می‌شود
		var requested = billing.startConnection() # است true در صورت شروع فرایند خروجی
	else:
		print("billing plugin not found")



func _reconnect_Button_Pressed():
	# اگر بعد از شروع بازی نتوانستیم به فروشگاه وصل بشویم
	# دکمه‌ای برای «اتصال مجدد» مشخص کنید و با کلیک روی آن این تابع را اجرا کنید
	# در اینجا باید اول دکمه «اتصال مجدد» را غیرفعال کنید
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
	# در این حالت اتصال به فروشگاه ناموفق بوده است
	# بهتر است در اینجا داخل یک دیالوگ به کاربر اطلاع بدهید
	# این می‌تواند به دلیل وصل نبودن اینترنت و یا نصب نبودن برنامه مایکت یا بازار باشد
	# در اینجا باید دکمه «اتصال مجدد» برای اتصال را فعال کنید و به کاربر نمایش دهید
    


func _on_QuerySucceed(results):
	# اطلاعات کالاها در پارامتر این تابع دریافت می‌شود
	# پارامتر این تابع آرایه‌ای از دیکشنری‌ها است کلیدهای هر دیکشنری به صورت زیر است
	# "product_id", "type", "price", "title", "description"
	for res in results:
		print(res)	
	# حالا می‌توانیم خریدها را به صورت زیر دریافت کنیم
	var _p = billing.queryPurchases()
	# آن است purchases خروجی این تابع یک دیکشنری است که خریدها به صورت یک آرایه در کلید
	# کلیدهای هر کدام از خریدها به صورت زیر است
	# "order_id", "package_name", "purchase_state", "purchase_time", "signature", "sku"
	# در اینجا اگر خریدی وجود دارد که مصرف شدنی است باید آن را مصرف کنید
	var purchases = _p["purchases"]
	for pur in purchases:
		if pur["sku"] == SKU_PREMIUM:
			# حالت پرمیوم قبلاً خریده شده آن را فعال می کنیم
			_setGamePremium()
			return
	# حالت پرمیوم قبلاً خریده نشده فرایند در صورت نیاز خرید آن را آغاز می‌کنیم
	billing.purchase(SKU_PREMIUM)
	        


func _on_QueryFailed():
	# اطلاعات کالاها و خریدهای کاربر دریافت نشد
	# تلاش مجدد برای دریافت اطلاعات
	billing.querySkuDetails()


func _setGamePremium():
	print("Game is premium now.")
	# بازی خریده شده است و حالت پرمیوم را برای کاربر فعال کنید
	# برای تجربه بهتر به نحوی به کاربر اطلاع دهید که بازی در حالت پرمیوم است
	# مثلاً داخل دیالوگ به کاربر اطلاع دهید
	


func _on_PurchaseSucceed(sku: String):
	print("Purchase Succeed SKU: ", sku)
	# در اینجا می‌توانید از یک دیالوگ برای نمایش نتیجه خرید استفاده کنید
	# اگر خرید مصرف شدنی نیست آن را برای کاربر فعال کنید
	# مصرف کنید consume ولی اگر خرید مصرف شدنی است ابتدا خرید را بوسیله تابع 
	var requested = billing.consume(sku) # استفاده کنید consume فقط اگر خرید مصرف شدنی بود از تابع 
    

func _on_PurchaseFailed(errorCode: int, errorMessage: String):
	print("Purchase Failed error code: " + str(errorCode) + " error message: " + errorMessage)



func _on_ConsumeSucceed(sku: String):
	print("Consume Succeed sku: ", sku)
	# این تابع فقط وقتی اجرا می‌شود که مصرف خرید موفقیت آمیز بوده باشد
	# پس در اینجا باید مورد خریده شده را در بازی ذخیره کنید
	# چون پس از مصرف خرید، کاربر در بازار یا مایکت دیگر صاحب آن نیست و می‌تواند دوباره آن را بخرد

func _on_ConsumeFailed(sku: String):
	print("Consume Failed sku: ", sku)
	# مصرف خرید ناموفق بود، تلاش مجدد برای مصرف خرید
	var requested = billing.consume(sku) # است true در صورت شروع فرایند مصرف، خروجی




```


---------------------------------------------------------------------------


**"Mohamadreza Amani"**  

My LinkedIn Profile: [https://www.linkedin.com/in/ygngy](https://www.linkedin.com/in/ygngy)

My Github Profile: [https://github.com/ygngy](https://github.com/ygngy)  

Email:  [help4usr@gmail.com](mailto:help4usr@gmail.com)
