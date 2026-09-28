# DistroMaster DMS — Distribution Management System

DistroMaster DMS is a professional Android Mobile Application for wholesale, retail, and FMCG product distribution businesses with complete bilingual support (English & বাংলা), sales billing, inventory management, routes tracking, and 58mm/80mm Bluetooth thermal receipts.

---

## 📱 GitHub থেকে APK ডাউনলোড করার সম্পূর্ণ নিয়ম (Step-by-Step Guide)

GitHub-এ আপনার প্রোজেক্ট পুশ করার পর **স্বয়ংক্রিয়ভাবে (Automatically)** APK তৈরি হয়ে ডাউনলোড করার জন্য রেডি হয়ে যাবে।

### ১. AI Studio থেকে GitHub-এ কোড Push করুন
1. AI Studio স্ক্রিনের উপরের ডানদিকের **Settings (গিয়ার আইকন)** অথবা **Export / Git** মেনুতে ক্লিক করুন।
2. **"Push to GitHub"** নির্বাচন করুন এবং আপনার GitHub একাউন্ট কানেক্ট করে একটি নতুন Repository তৈরি করে কোড পুশ করে দিন।

### ২. GitHub Actions দ্বারা স্বয়ংক্রিয় APK বিল্ড
- কোড GitHub-এ পুশ হওয়ার সাথে সাথেই `.github/workflows/build-apk.yml` ফাইলটির মাধ্যমে GitHub Actions অটোমেটিক APK তৈরি করা শুরু করবে (সাধারণত ২-৩ মিনিট সময় নেয়)।

### ৩. GitHub থেকে APK ফাইল ডাউনলোড করার নিয়ম:
1. আপনার GitHub Repository-এর পেজে যান।
2. পেজের উপরের মেনু থেকে **"Actions"** ট্যাবে ক্লিক করুন।
3. তালিকার সবচেয়ে উপরের **"Build & Release Android APK"** (সবুজ টিক চিহ্নযুক্ত) রানে ক্লিক করুন।
4. পেজটির একদম নিচের দিকে স্ক্রোল করলে **"Artifacts"** সেকশন দেখতে পাবেন।
5. সেখানে **`DistroMaster-DMS-APK`** নামের ফাইলে ক্লিক করলেই আপনার ফোনে বা কম্পিউটারে APK জিপ ফাইলটি ডাউনলোড হয়ে যাবে।
6. জিপটি আনজিপ করে `DistroMaster-DMS-Debug.apk` ফাইলটি যেকোনো অ্যান্ড্রয়েড ফোনে ইনস্টল করে ব্যবহার করুন।

---

## ⚡ সরাসরি AI Studio থেকে APK ডাউনলোড করার শর্টকাট (Fastest Way)
GitHub ছাড়াও আপনি সরাসরি এই AI Studio থেকেই ১ ক্লিকে APK তৈরি করতে পারেন:
1. AI Studio-এর উপরের ডানপাশের **Settings (কোকিল/গিয়ার মেনু)**-তে যান।
2. **"Generate APK / AAB"** অপশনে ক্লিক করুন।
3. কয়েক সেকেন্ডের মধ্যে আপনার ব্রাউজারে সরাসরি APK ফাইলটি ডাউনলোড হয়ে যাবে!

---

## 🛠 Features
- **ড্যাশবোর্ড (Dashboard):** মোট বিক্রি, গ্রাহক বকেয়া (Customer Due), সাপ্লায়ার বকেয়া (Supplier Due), ফেরত পণ্য, মোট খরচ ও বর্তমান স্টক ভ্যালুয়েশন।
- **বিক্রি ও বিলিং (Sales & Billing):** ফিল্টার (আজ, চলতি মাস, বছর, বকেয়া/পরিশোধ), New Sale +, চালান সার্চ, ও পিডিএফ এক্সপোর্ট।
- **সাপ্লায়ার ও ক্রয় (Purchases):** নতুন ক্রয় এন্ট্রি, সাপ্লায়ার হিসাব, পেইড ও ডিউ ট্র্যাকিং।
- **পণ্য ও ইনভেন্টরি (Inventory):** বারকোড স্ক্যানার, লো-স্টক অ্যালার্ট, ক্রয় ও বিক্রয় রেট, সম্ভাব্য মুনাফা হিসাব।
- **রুট ও ডেলিভারি ট্র্যাকিং (Delivery Pipeline):** রুট ভিত্তিক সেলস রিপ্রেজেন্টেটিভ (SR) অ্যাসাইন, অর্ডার ডেলিভারি পাইপলাইন (*Order Taken ➔ Packed ➔ In Transit ➔ Delivered*)।
- **ব্লুটুথ থার্মাল রশিদ (Thermal Bluetooth Printing):** ৫৮মিমি/৮০মিমি প্রিন্ট প্রিভিউ, ডিজিটাল শেয়ারিং ও বকেয়া আদায়।
- **দ্বিভাষিক (Bilingual):** ইংরেজি ও বাংলা ভাষার ইনস্ট্যান্ট টগল (`বাং` / `EN`)।
