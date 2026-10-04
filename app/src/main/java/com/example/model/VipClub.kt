package com.example.model

enum class VipTier(
    val level: Int,
    val nameBn: String,
    val nameEn: String,
    val minPoints: Int,
    val badgeEmoji: String,
    val dailyBonus: Long,
    val perkBn: String,
    val perkEn: String
) {
    BRONZE(
        1,
        "ব্রোঞ্জ মেম্বার",
        "Bronze Member",
        0,
        "🥉",
        10000L,
        "দৈনিক ৳১০,০০০ উপহার এবং মৌলিক টেবিল সুবিধা",
        "Daily ৳10,000 bonus & basic table access"
    ),
    SILVER(
        2,
        "সিলভার প্লেয়ার",
        "Silver Player",
        500,
        "🥈",
        15000L,
        "দৈনিক ৳১৫,০০০ উপহার, ৫% ক্যাশব্যাক পয়েন্ট",
        "Daily ৳15,000 bonus & 5% extra VIP points"
    ),
    GOLD(
        3,
        "গোল্ড ভিআইপি",
        "Gold VIP",
        2000,
        "🥇",
        25000L,
        "দৈনিক ৳২৫,০০০ উপহার, হাই রোলার আনলক",
        "Daily ৳25,000 bonus & high-roller room unlock"
    ),
    PLATINUM(
        4,
        "প্লাটিনাম এলিট",
        "Platinum Elite",
        5000,
        "💎",
        50000L,
        "দৈনিক ৳৫০,০০০ উপহার, ২ গুণ বেশি লাকি স্পিন রিওয়ার্ড",
        "Daily ৳50,000 bonus & 2x Wheel reward boosts"
    ),
    ROYAL_NAWAB(
        5,
        "বাংলার রাজকীয় নবাব",
        "Royal Nawab of Bengal",
        10000,
        "👑",
        100000L,
        "দৈনিক ৳১,০০,০০০ উপহার, রাজকীয় গোল্ডেন চিপ ও নবাব খেতাব",
        "Daily ৳100,000 bonus, Royal Golden Chip & Nawab Title"
    );

    companion object {
        fun fromLevel(level: Int): VipTier {
            return values().firstOrNull { it.level == level } ?: BRONZE
        }
    }
}
