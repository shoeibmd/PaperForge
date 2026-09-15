import { Select } from '@mantine/core';
import { useTranslation } from 'react-i18next';
import { IconLanguage } from '@tabler/icons-react';

const LANGUAGES = [
  { value: 'en', label: 'English' },
  { value: 'ta', label: 'தமிழ் (Tamil)' },
  { value: 'hi', label: 'हिन्दी (Hindi)' },
  { value: 'ml', label: 'മലയാളം (Malayalam)' },
  { value: 'te', label: 'తెలుగు (Telugu)' },
  { value: 'kn', label: 'ಕನ್ನಡ (Kannada)' },
  { value: 'fr', label: 'Français' },
  { value: 'de', label: 'Deutsch' },
  { value: 'es', label: 'Español' },
  { value: 'ar', label: 'العربية (Arabic)' },
];

export function LanguageSwitcher() {
  const { i18n } = useTranslation();

  const handleLanguageChange = (value: string | null) => {
    if (value) {
      i18n.changeLanguage(value);
      const dir = value === 'ar' ? 'rtl' : 'ltr';
      document.documentElement.dir = dir;
      document.documentElement.lang = value;
    }
  };

  return (
    <Select
      size="xs"
      leftSection={<IconLanguage size={14} />}
      data={LANGUAGES}
      value={i18n.language ? i18n.language.split('-')[0] : 'en'}
      onChange={handleLanguageChange}
      style={{ width: 140 }}
      comboboxProps={{ withinPortal: false }}
    />
  );
}
