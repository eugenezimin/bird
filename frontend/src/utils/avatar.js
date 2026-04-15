const PALETTE = [
  { bg: '#1d4ed8', text: '#dbeafe' },
  { bg: '#7c3aed', text: '#ede9fe' },
  { bg: '#059669', text: '#d1fae5' },
  { bg: '#dc2626', text: '#fee2e2' },
  { bg: '#d97706', text: '#fef3c7' },
  { bg: '#0891b2', text: '#cffafe' },
  { bg: '#be185d', text: '#fce7f3' },
  { bg: '#65a30d', text: '#ecfccb' },
  { bg: '#0e7490', text: '#cffafe' },
  { bg: '#9333ea', text: '#f3e8ff' },
];

export function avatarColors(name = '') {
  const idx = [...name].reduce((a, c) => a + c.charCodeAt(0), 0) % PALETTE.length;
  return PALETTE[idx];
}

export function initials(name = '') {
  return name.trim().split(/\s+/).slice(0, 2).map(w => w[0]).join('').toUpperCase();
}
