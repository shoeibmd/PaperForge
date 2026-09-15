import i18n from 'i18next';
import { initReactI18next } from 'react-i18next';
import LanguageDetector from 'i18next-browser-languagedetector';

import en from './locales/en/translation.json';
import ta from './locales/ta/translation.json';
import hi from './locales/hi/translation.json';
import ml from './locales/ml/translation.json';
import te from './locales/te/translation.json';
import kn from './locales/kn/translation.json';
import fr from './locales/fr/translation.json';
import de from './locales/de/translation.json';
import es from './locales/es/translation.json';
import ar from './locales/ar/translation.json';

const resources = {
  en: { translation: en },
  ta: { translation: ta },
  hi: { translation: hi },
  ml: { translation: ml },
  te: { translation: te },
  kn: { translation: kn },
  fr: { translation: fr },
  de: { translation: de },
  es: { translation: es },
  ar: { translation: ar },
};

i18n
  .use(LanguageDetector)
  .use(initReactI18next)
  .init({
    resources,
    fallbackLng: 'en',
    interpolation: {
      escapeValue: false,
    },
    detection: {
      order: ['localStorage', 'navigator'],
      caches: ['localStorage'],
    },
  });

i18n.on('languageChanged', (lng) => {
  const dir = lng === 'ar' ? 'rtl' : 'ltr';
  document.documentElement.dir = dir;
  document.documentElement.lang = lng;
});

export default i18n;
