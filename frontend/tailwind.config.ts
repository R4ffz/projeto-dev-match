import type { Config } from 'tailwindcss';

const config: Config = {
  content: ['./index.html', './src/**/*.{ts,tsx}'],
  theme: {
    extend: {
      fontFamily: {
        sans: ['Inter', 'ui-sans-serif', 'system-ui', 'sans-serif'],
        mono: ['"JetBrains Mono"', 'ui-monospace', 'SFMono-Regular', 'monospace'],
      },
      letterSpacing: {
        tightish: '-0.01em',
        tight2: '-0.02em',
      },
      boxShadow: {
        focus: '0 0 0 3px rgba(15, 23, 42, 0.12)',
      },
    },
  },
  plugins: [],
};

export default config;
