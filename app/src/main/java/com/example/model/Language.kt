package com.example.model

enum class AppLanguage {
    BN, EN
}

object Strings {
    fun appTitle(lang: String) = if (lang == "BN") "ঢাকা রয়্যাল ক্যাসিনো" else "Dhaka Royale Casino"
    fun currency() = "৳"
    fun balance(lang: String) = if (lang == "BN") "ব্যালেন্স" else "Balance"
    fun vipNawab(lang: String) = if (lang == "BN") "নবাব ক্লাব" else "Nawab VIP"
    fun dailyBonus(lang: String) = if (lang == "BN") "দৈনিক উপহার" else "Daily Bonus"
    fun claim(lang: String) = if (lang == "BN") "গ্রহণ করুন" else "Claim"
    fun claimed(lang: String) = if (lang == "BN") "সংগৃহীত" else "Claimed"
    fun freeCoins(lang: String) = if (lang == "BN") "ফ্রি কয়েন" else "Free Coins"
    fun deposit(lang: String) = if (lang == "BN") "রিচার্জ (ফ্রি)" else "Top-up (Free)"
    fun history(lang: String) = if (lang == "BN") "ইতিহাস" else "History"
    
    // Games
    fun crashTitle(lang: String) = if (lang == "BN") "উড়োজাহাজ ক্র্যাশ" else "Aviator Crash"
    fun crashSubtitle(lang: String) = if (lang == "BN") "রকেট ওড়ার আগে ক্যাশআউট করুন!" else "Cash out before the plane flies away!"
    
    fun teenPattiTitle(lang: String) = if (lang == "BN") "তিন পাত্তি রয়্যাল" else "Teen Patti Royale"
    fun teenPattiSubtitle(lang: String) = if (lang == "BN") "খাঁটি বাংলাদেশি কার্ডের লড়াই" else "Authentic 3-Card Showdown"
    
    fun andarBaharTitle(lang: String) = if (lang == "BN") "আন্দর বাহার" else "Andar Bahar"
    fun andarBaharSubtitle(lang: String) = if (lang == "BN") "জোকার কার্ডের দ্রুত পূর্বাভাস" else "Fast Joker Card Prediction"
    
    fun slotsTitle(lang: String) = if (lang == "BN") "৭৭৭ গোল্ডেন স্লট" else "777 Golden Slots"
    fun slotsSubtitle(lang: String) = if (lang == "BN") "রয়্যাল মেগা জ্যাকপট স্পিন" else "Royal Mega Jackpot Spin"
    
    fun wheelTitle(lang: String) = if (lang == "BN") "ভাগ্য চাকা" else "Lucky Fortune Wheel"
    fun wheelSubtitle(lang: String) = if (lang == "BN") "ঘুরিয়ে জিতুন ৳৫০,০০০ পর্যন্ত" else "Spin & win up to ৳50,000"

    // Actions
    fun bet(lang: String) = if (lang == "BN") "বাজি" else "Bet"
    fun placeBet(lang: String) = if (lang == "BN") "বাজি ধরুন" else "Place Bet"
    fun cashOut(lang: String) = if (lang == "BN") "ক্যাশ আউট" else "Cash Out"
    fun spin(lang: String) = if (lang == "BN") "স্পিন" else "Spin"
    fun auto(lang: String) = if (lang == "BN") "অটো" else "Auto"
    fun chaal(lang: String) = if (lang == "BN") "চাল" else "Chaal"
    fun pack(lang: String) = if (lang == "BN") "প্যাক (ফোল্ড)" else "Pack"
    fun show(lang: String) = if (lang == "BN") "শো (দেখান)" else "Show"
    fun seeCards(lang: String) = if (lang == "BN") "কার্ড দেখুন" else "See Cards"
    fun blind(lang: String) = if (lang == "BN") "ব্লাইন্ড" else "Blind"
    fun pot(lang: String) = if (lang == "BN") "পট" else "Pot"
    
    // Warnings & Disclaimers
    fun disclaimer(lang: String) = if (lang == "BN") 
        "⚠️ সতর্কবার্তা: এটি শুধুমাত্র ভার্চুয়াল বিনোদনের খেলা। এখানে কোনো আসল টাকার জুয়া বা আর্থিক লেনদেন নেই।"
    else 
        "⚠️ Notice: For virtual entertainment only. No real money gambling or actual monetary payouts."

    fun insufficientBalance(lang: String) = if (lang == "BN") 
        "পর্যাপ্ত ব্যালেন্স নেই! ওয়ালেট থেকে ফ্রি কয়েন সংগ্রহ করুন।" 
    else 
        "Insufficient balance! Collect free coins from wallet."
}
