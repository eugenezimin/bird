/**
 * Unwrap the standard API envelope { code, message, data }.
 * Throws if the code is not 2xx.
 */
export function extractData(response) {
  const envelope = response.data;
  if (!envelope.code?.startsWith('2')) {
    throw new Error(envelope.message ?? 'Unknown error');
  }
  return envelope.data;
}
