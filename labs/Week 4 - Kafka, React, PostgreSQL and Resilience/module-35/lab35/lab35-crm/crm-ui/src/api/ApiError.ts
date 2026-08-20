export class ApiError extends Error {
  constructor(
      public readonly status: number,
      public readonly code: string,
      message: string,
      public readonly fieldErrors?: Record<string, string>,
  ) {
    super(message)
    this.name = 'ApiError'
  }

  static async from(response: Response): Promise<ApiError> {
    let body: any = null
    try {
      body = await response.json()
    } catch {
      // no JSON body — fall through with a generic message below
    }
    const message = body?.message ?? `Request failed with status ${response.status}`
    const code = body?.error ?? (response.status >= 500 ? 'SERVER_ERROR' : 'REQUEST_ERROR')
    const fieldErrors = normalizeViolations(body?.violations)
    return new ApiError(response.status, code, message, fieldErrors)
  }
}

// Your real GlobalExceptionHandler (Lab 29/31) returns `violations` as an
// array of { field, message } objects, not a ready-made field->message map.
// Verify this against your own Step 1 curl output on a forced 400 (Step 8
// below) and adjust the property names here if your backend differs.
function normalizeViolations(violations: unknown): Record<string, string> | undefined {
  if (!Array.isArray(violations)) return undefined
  const map: Record<string, string> = {}
  for (const v of violations) {
    if (v && typeof v === 'object' && 'field' in v && 'message' in v) {
      map[String((v as any).field)] = String((v as any).message)
    }
  }
  return Object.keys(map).length ? map : undefined
}