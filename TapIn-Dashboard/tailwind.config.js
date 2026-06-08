/** @type {import('tailwindcss').Config} */
export default {
  content: ['./index.html', './src/**/*.{js,jsx}'],
  theme: {
    extend: {
      colors: {
        navy: {
          DEFAULT: '#1F3864',
          dark:    '#162848',
          light:   '#2A4A82',
        },
        brand: {
          DEFAULT: '#2E5FAC',
          light:   '#4A7FCE',
          surface: '#E8EEF9',
        },
      },
      fontFamily: {
        sans:    ['"DM Sans"', 'sans-serif'],
        display: ['"Syne"', 'sans-serif'],
        mono:    ['"DM Mono"', 'monospace'],
      },
      boxShadow: {
        card:  '0 2px 8px 0 rgba(31,56,100,0.08)',
        panel: '0 4px 24px 0 rgba(31,56,100,0.12)',
        glow:  '0 0 0 3px rgba(46,95,172,0.20)',
      },
      animation: {
        'fade-in':   'fadeIn 0.4s ease both',
        'slide-up':  'slideUp 0.4s cubic-bezier(.16,1,.3,1) both',
        'pulse-dot': 'pulseDot 2s ease-in-out infinite',
      },
      keyframes: {
        fadeIn:   { from: { opacity: 0 }, to: { opacity: 1 } },
        slideUp:  { from: { opacity: 0, transform: 'translateY(16px)' }, to: { opacity: 1, transform: 'translateY(0)' } },
        pulseDot: { '0%,100%': { opacity: 1, transform: 'scale(1)' }, '50%': { opacity: 0.5, transform: 'scale(1.4)' } },
      },
    },
  },
  plugins: [],
}
