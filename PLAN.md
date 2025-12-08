# Age Calculator Build Plan

Goal: rebuild the Age Calculator app to match the legacy screen (screenshot) with a modern, reliable implementation.

## Scope & Features
- Select birth date (day, month, year) via dropdowns or native pickers.
- Select current date defaulting to today, but editable for custom calculations.
- Calculate age in years, months, and days; show result and any validation errors.
- Persist last-used inputs during a session; clear/reset option.
- Keep a single-screen flow with a prominent calculate action.

## UI & UX Notes
- Layout: header bar with title and settings icon; welcome greeting with live clock; two labeled date rows (Birth Date, Current Date); large primary CTA; helper text; small footer credit.
- Style: dark green background; contrasting header (blue) and CTA (red); legible typography with clear label hierarchy.
- Accessibility: tap targets ≥48dp, readable contrast, support screen readers with content descriptions.

## Implementation Steps
1) Project setup: confirm Gradle builds; set app theme colors, typography, and base styles.
2) UI layout: build the main screen with header, greeting clock, date input rows, calculate button, helper text, and footer.
3) Date input behavior: use native date pickers; prefill current date with today; constrain birth date <= current date.
4) Age calculation logic: compute years/months/days correctly across leap years and month boundaries; handle invalid ranges with inline errors.
5) State handling: store selected dates in ViewModel/state; reflect updates immediately in UI; add reset/clear option if desired.
6) Feedback: show result and/or error message area beneath the button; disable calculate when inputs invalid.
7) Testing: unit test the age calculation utility; UI test for date selection and result rendering.
8) Polish: add settings icon stub, footer text, and small animations on button press; verify on multiple screen sizes.

## Deliverables
- Main screen UI matching the provided layout and colors.
- Age calculation utility with tests.
- Documented behaviors for validation and edge cases.
