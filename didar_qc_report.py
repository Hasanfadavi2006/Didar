import requests
import json
from datetime import datetime

API_KEY = "231020a4-665d-446e-b5d1-2ca5817b4662"
BASE_URL = "https://app.didar.me/api"
KEYWORD = "برگشت به qc"

def search_activities(keyword, page_from=0, limit=100):
    url = f"{BASE_URL}/activity/search?apikey={API_KEY}"
    payload = {
        "Criteria": {
            "From": page_from,
            "Limit": limit,
            "Keywords": keyword
        }
    }
    resp = requests.post(url, json=payload, timeout=30)
    resp.raise_for_status()
    return resp.json()

def search_deals(keyword, page_from=0, limit=100):
    url = f"{BASE_URL}/deal/search?apikey={API_KEY}"
    payload = {
        "Criteria": {
            "From": page_from,
            "Limit": limit,
            "Keywords": keyword
        }
    }
    resp = requests.post(url, json=payload, timeout=30)
    resp.raise_for_status()
    return resp.json()

def fetch_all(search_fn, keyword):
    results = []
    page = 0
    limit = 100
    while True:
        data = search_fn(keyword, page_from=page, limit=limit)
        items = data.get("ResponseData", data.get("Data", []))
        if not items:
            break
        results.extend(items)
        if len(items) < limit:
            break
        page += limit
    return results

def print_report(title, items, fields):
    print(f"\n{'='*60}")
    print(f"  {title}  ({len(items)} مورد)")
    print('='*60)
    if not items:
        print("  موردی یافت نشد.")
        return
    for i, item in enumerate(items, 1):
        print(f"\n--- #{i} ---")
        for label, key in fields:
            val = item.get(key, "")
            if val:
                print(f"  {label}: {val}")

def main():
    print(f"جستجو برای: «{KEYWORD}»")
    print(f"زمان گزارش: {datetime.now().strftime('%Y-%m-%d %H:%M:%S')}")

    # فعالیت‌ها
    print("\nدر حال دریافت فعالیت‌ها...")
    try:
        activities = fetch_all(search_activities, KEYWORD)
        print_report("فعالیت‌های «برگشت به QC»", activities, [
            ("عنوان",       "Title"),
            ("توضیحات",     "Description"),
            ("نوع",         "ActivityTypeName"),
            ("تاریخ",       "ActivityDate"),
            ("مخاطب",       "ContactName"),
            ("معامله",      "DealTitle"),
            ("مالک",        "OwnerName"),
            ("وضعیت",       "StatusName"),
        ])
    except Exception as e:
        print(f"  خطا در دریافت فعالیت‌ها: {e}")

    # معاملات
    print("\nدر حال دریافت معاملات...")
    try:
        deals = fetch_all(search_deals, KEYWORD)
        print_report("معاملات «برگشت به QC»", deals, [
            ("عنوان",       "Title"),
            ("توضیحات",     "Description"),
            ("مرحله",       "PipelineStageName"),
            ("وضعیت",       "StatusName"),
            ("مبلغ",        "Amount"),
            ("مخاطب",       "ContactName"),
            ("مالک",        "OwnerName"),
            ("تاریخ ایجاد", "CreateDate"),
        ])
    except Exception as e:
        print(f"  خطا در دریافت معاملات: {e}")

    # ذخیره خروجی JSON
    output_file = f"qc_report_{datetime.now().strftime('%Y%m%d_%H%M%S')}.json"
    try:
        with open(output_file, "w", encoding="utf-8") as f:
            json.dump({
                "keyword": KEYWORD,
                "generated_at": datetime.now().isoformat(),
                "activities": activities if 'activities' in dir() else [],
                "deals": deals if 'deals' in dir() else [],
            }, f, ensure_ascii=False, indent=2)
        print(f"\nخروجی JSON ذخیره شد: {output_file}")
    except Exception as e:
        print(f"  خطا در ذخیره فایل: {e}")

if __name__ == "__main__":
    main()
