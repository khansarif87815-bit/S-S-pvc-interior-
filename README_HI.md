# S S PVC INTERIOR — Android starter app

यह Android Studio के लिए Kotlin + Jetpack Compose प्रोजेक्ट है।

## चलाने का तरीका
1. Android Studio में इस पूरे फ़ोल्डर को Open करें।
2. Gradle Sync पूरा होने दें।
3. Android फोन को USB debugging से जोड़ें या Emulator चलाएँ।
4. Run ▶ दबाएँ।

## अभी शामिल सुविधाएँ
- Premium dark + gold ब्रांड UI
- PVC Interior की 15 कैटेगरी
- डिजाइन सर्च और कैटेगरी फ़िल्टर
- डिजाइन डिटेल स्क्रीन
- पसंदीदा डिजाइन (वर्तमान सत्र)
- WhatsApp enquiry share
- Owner Admin Panel
- Admin से डिजाइन जोड़ना और हटाना
- जोड़े/हटाए गए डिजाइन फोन में SharedPreferences के जरिए सेव होते हैं

## Owner PIN
डेमो PIN: `2580`
इसे रिलीज़ से पहले बदलें। यह शुरुआती डेमो लॉगिन है, मजबूत ऑनलाइन सुरक्षा नहीं।

## महत्वपूर्ण सीमाएँ
यह पहला चलने योग्य starter version है, पूर्ण production ऐप नहीं।
- डिजाइनों में अभी demo artwork placeholders हैं; असली PVC panel photos Admin image upload/gallery से जोड़ने के लिए अगला चरण चाहिए।
- कीमतें और डिजाइन स्थानीय फोन में सेव होते हैं; cloud sync / multi-device admin के लिए backend चाहिए।
- WhatsApp लिंक enquiry text खोलता है; अपना business WhatsApp नंबर जोड़ना बाकी है।
- Play Store release के लिए app icon, privacy policy, signing key और final testing चाहिए।

## अगला विकास चरण
Firebase/Supabase backend, सुरक्षित owner login, image upload, product dimensions/thickness/finish, quotation calculator, customer enquiry/order tracking और Hindi/English language switch जोड़ें।

## Official branding added
आपकी भेजी हुई S S PVC Interior and Furniture वाली image अब ऐप के header में लगाई गई है।
Image में दिए गए संपर्क नंबर Home banner में भी जोड़े गए हैं:
- Sarif Khan: 6387351056
- Jubar Khan: 93528 15026
- Tumeer Khan: 97939 09721

## APK build via GitHub Actions
इस runtime में Android SDK और Gradle उपलब्ध नहीं हैं, इसलिए यहाँ APK compile नहीं किया जा सका।
`.github/workflows/build-apk.yml` workflow जोड़ा गया है। पूरा source GitHub repository में upload करें।
फिर GitHub में Actions → Build S S PVC INTERIOR APK → Run workflow चलाएँ।
Build पूरा होने पर workflow run के Artifacts भाग से `ss-pvc-interior-debug-apk` डाउनलोड करें।
यह debug APK है; Play Store / public distribution के लिए अलग release signing और testing जरूरी है।
