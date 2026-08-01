package com.example.ui.language

object Strings {
    fun get(key: String, language: String): String {
        val isBn = language.uppercase() == "BN"
        return when (key) {
            // App & TopBar
            "app_title" -> if (isBn) "ট্যালি খাতা" else "Tally Khata"
            "sub_title" -> if (isBn) "ডিজিটাল ক্যাশ ও হিসাব খাতা" else "Digital Ledger & Cash Book"
            "business" -> if (isBn) "ব্যবসা প্রতিষ্ঠান" else "Business"
            "switch_business" -> if (isBn) "ব্যবসা পরিবর্তন" else "Switch Business"
            "add_business" -> if (isBn) "নতুন ব্যবসা যোগ করুন" else "Add New Business"
            "language" -> if (isBn) "ভাষা (Language)" else "Language"
            "pin_lock" -> if (isBn) "পিন লক" else "PIN Lock"
            "settings" -> if (isBn) "সেটিংস" else "Settings"

            // Dashboard Summary
            "total_customers" -> if (isBn) "মোট কাস্টমার" else "Total Customers"
            "total_receivable" -> if (isBn) "মোট পাবো" else "Total Receivable"
            "total_payable" -> if (isBn) "মোট দেবো" else "Total Payable"
            "net_cashflow" -> if (isBn) "ক্যাশফ্লো স্থিতি" else "Net Cashflow"
            "today_summary" -> if (isBn) "আজকের হিসাব" else "Today's Summary"
            "collected_today" -> if (isBn) "আজ পেয়েছি" else "Collected Today"
            "given_today" -> if (isBn) "আজ দিয়েছি" else "Given Today"
            "pabo_badge" -> if (isBn) "পাবো" else "GET"
            "debo_badge" -> if (isBn) "দেবো" else "GIVE"

            // Actions & Navigation
            "nav_home" -> if (isBn) "ড্যাশবোর্ড" else "Dashboard"
            "nav_customers" -> if (isBn) "গ্রাহক / খাতা" else "Customers"
            "nav_reports" -> if (isBn) "রিপোর্ট" else "Reports"
            "nav_profile" -> if (isBn) "প্রোফাইল" else "Profile"
            "add_customer" -> if (isBn) "নতুন কাস্টমার যোগ" else "Add Customer"
            "add_entry" -> if (isBn) "নতুন লেনদেন" else "Add Entry"
            "give_credit" -> if (isBn) "পাবো (+)" else "Give Credit (+)"
            "collect_cash" -> if (isBn) "দেবো (-)" else "Collect Cash (-)"
            "calculator" -> if (isBn) "ক্যালকুলেটর" else "Calculator"
            "qr_code" -> if (isBn) "QR কোড" else "QR Code"
            "voice_note" -> if (isBn) "ভয়েস নোট" else "Voice Note"
            "receipt_pdf" -> if (isBn) "রশিদ ডাউনলোড" else "Download Receipt"

            // Search & Filters
            "search_hint" -> if (isBn) "কাস্টমার বা মোবাইল নম্বর দিয়ে খুঁজুন..." else "Search by customer name or phone..."
            "all" -> if (isBn) "সকল" else "All"
            "receivable_only" -> if (isBn) "পাবো (দেনাদার)" else "Receivables"
            "payable_only" -> if (isBn) "দেবো (পাওনাদার)" else "Payables"
            "favorites" -> if (isBn) "প্রিয় কাস্টমার" else "Favorites"
            "recent_transactions" -> if (isBn) "সাম্প্রতিক লেনদেনসমূহ" else "Recent Transactions"
            "no_transactions" -> if (isBn) "কোন লেনদেন পাওয়া যায়নি" else "No transactions found"

            // Customer Details
            "customer_detail" -> if (isBn) "গ্রাহকের খাতা" else "Customer Ledger"
            "customer_name" -> if (isBn) "কাস্টমারের নাম" else "Customer Name"
            "mobile_number" -> if (isBn) "মোবাইল নম্বর" else "Mobile Number"
            "address" -> if (isBn) "ঠিকানা" else "Address"
            "call" -> if (isBn) "কল করুন" else "Call"
            "sms_whatsapp" -> if (isBn) "তাগাদা (SMS/WA)" else "Send Reminder"
            "delete_customer" -> if (isBn) "কাস্টমার মুছুন" else "Delete Customer"
            "running_balance" -> if (isBn) "বর্তমান জের / স্থিতি" else "Running Balance"

            // Dialogs & Forms
            "enter_amount" -> if (isBn) "টাকার পরিমাণ" else "Enter Amount"
            "payment_method" -> if (isBn) "পরিশোধের মাধ্যম" else "Payment Method"
            "cash" -> if (isBn) "নগদ ক্যাশ" else "Cash"
            "bkash" -> if (isBn) "বিকাশ (bKash)" else "bKash"
            "nagad" -> if (isBn) "নগদ অ্যাপ (Nagad)" else "Nagad"
            "rocket" -> if (isBn) "রকেট (Rocket)" else "Rocket"
            "bank" -> if (isBn) "ব্যাংক ট্রান্সফার" else "Bank Transfer"
            "goods" -> if (isBn) "বাকীতে পণ্য বিক্রয়" else "Due Goods"
            "notes_optional" -> if (isBn) "বিবরণ / নোট (ঐচ্ছিক)" else "Notes / Description (Optional)"
            "save" -> if (isBn) "সংরক্ষণ করুন" else "Save"
            "cancel" -> if (isBn) "বাতিল" else "Cancel"
            "confirm_delete" -> if (isBn) "আপনি কি নিশ্চিত মুছে ফেলতে চান?" else "Are you sure you want to delete?"

            // Security PIN
            "security_pin" -> if (isBn) "নিরাপত্তা পিন কোড" else "Security PIN Code"
            "enter_pin" -> if (isBn) "৪ ডিজিটের পিন নম্বর দিন" else "Enter 4-Digit PIN Code"
            "wrong_pin" -> if (isBn) "ভুল পিন! পুনরায় চেষ্টা করুন" else "Incorrect PIN! Try again."
            "unlock" -> if (isBn) "আনলক করুন" else "Unlock"
            "enable_pin" -> if (isBn) "পিন নিরাপত্তা চালূ করুন" else "Enable Security PIN"

            // Profile & Reports
            "business_info" -> if (isBn) "ব্যবসার তথ্য" else "Business Details"
            "owner_name" -> if (isBn) "মালিকের নাম" else "Owner Name"
            "reports_title" -> if (isBn) "হিসাব ও বিশ্লেষণ রিপোর্ট" else "Financial Reports & Analytics"
            "export_pdf" -> if (isBn) "PDF ফাইল ডাউনলোড" else "Export PDF Report"
            "share_receipt" -> if (isBn) "রশিদ শেয়ার করুন" else "Share Receipt"
            "dark_mode" -> if (isBn) "নাইট মোড / ডার্ক থিম" else "Dark Theme"
            "backup_data" -> if (isBn) "ক্লাউড ব্যাকআপ ও ব্যাকআপ ডেটা" else "Cloud Sync & Backup"

            else -> key
        }
    }
}
