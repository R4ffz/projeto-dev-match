import type { ApiErrorBody, ApiFieldError } from '../types/error';

const BASE_URL = import.meta.env.VITE_API_BASE_URL;
export const TOKEN_STORAGE_KEY = 'devmatch_token';

if (!BASE_URL) {
  // eslint-disable-next-line no-console
  console.warn('VITE_API_BASE_URL nao definido. Defina em .env antes do build.');
}

export class ApiClientError extends Error {
  readonly status: number;
  readonly error: string;
  readonly fieldErrors?: ApiFieldError[];

  constructor(status: number, error: string, message: string, fieldErrors?: ApiFieldError[]) {
    super(message);
    this.name = 'ApiClientError';
    this.status = status;
    this.error = error;
    this.fieldErrors = fieldErrors;
  }

  /** Atalho para descobrir se o erro veio de validacao (campos invalidos). */
  get isValidation(): boolean {
    return this.status === 400 && Array.isArray(this.fieldErrors) && this.fieldErrors.length > 0;
  }
}

type Method = 'GET' | 'POST' | 'PUT' | 'DELETE';

async function request<T>(method: Method, path: string, body?: unknown): Promise<T> {
  const token = localStorage.getItem(TOKEN_STORAGE_KEY);

  const headers: Record<string, string> = {
    'Content-Type': 'application/json',
    Accept: 'application/json',
  };
  if (token) {
    headers.Authorization = `Bearer ${token}`;
  }

  let response: Response;
  try {
    response = await fetch(`${BASE_URL}${path}`, {
      method,
      headers,
      body: body !== undefined ? JSON.stringify(body) : undefined,
    });
  } catch (networkError) {
    throw new ApiClientError(
      0,
      'Network Error',
      'Nao foi possivel conectar ao servidor. Verifique sua conexao.',
    );
  }

  // 401: limpa token e dispara evento global para o AuthContext deslogar
  if (response.status === 401) {
    localStorage.removeItem(TOKEN_STORAGE_KEY);
    window.dispatchEvent(new CustomEvent('devmatch:unauthorized'));
    throw new ApiClientError(401, 'Unauthorized', 'Sessao expirada. Faca login novamente.');
  }

  if (!response.ok) {
    const errorBody = await parseErrorBody(response);
    throw new ApiClientError(
      response.status,
      errorBody?.error ?? 'Error',
      errorBody?.message ?? `Erro ${response.status}`,
      errorBody?.fieldErrors,
    );
  }

  // 204 ou body vazio
  if (response.status === 204 || response.headers.get('content-length') === '0') {
    return undefined as T;
  }

  const text = await response.text();
  if (!text) {
    return undefined as T;
  }
  return JSON.parse(text) as T;
}

async function parseErrorBody(response: Response): Promise<ApiErrorBody | null> {
  try {
    const text = await response.text();
    if (!text) return null;
    return JSON.parse(text) as ApiErrorBody;
  } catch {
    return null;
  }
}

/** Monta query string somente com valores definidos e nao vazios. */
export function buildQueryString(params: Record<string, string | number | undefined | null>): string {
  const search = new URLSearchParams();
  for (const [key, value] of Object.entries(params)) {
    if (value === undefined || value === null || value === '') continue;
    search.set(key, String(value));
  }
  const qs = search.toString();
  return qs ? `?${qs}` : '';
}

export const apiClient = {
  get: <T>(path: string) => request<T>('GET', path),
  post: <T>(path: string, body?: unknown) => request<T>('POST', path, body),
  put: <T>(path: string, body?: unknown) => request<T>('PUT', path, body),
  delete: <T>(path: string) => request<T>('DELETE', path),
};
