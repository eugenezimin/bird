export function extractData(response) {
  const envelope = response?.data;
  if (!envelope) throw new Error('No response data');
  if (!String(envelope.code).startsWith('2')) {
    throw new Error(envelope.message ?? 'Request failed');
  }
  return envelope.data;
}

export function safeExtract(response, fallback = null) {
  try { return extractData(response); }
  catch { return fallback; }
}
