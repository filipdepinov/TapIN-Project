# TapIn Dashboard

React + Vite + TailwindCSS admin/teacher/student dashboard for the TapIn NFC Attendance System.

## Prerequisites
- Node.js 18+
- TapIn Backend running on port 3000

## Setup

```bash
npm install
npm run dev
```

Dashboard runs at: http://localhost:5173  
Vite proxies all `/api/*` requests to `http://localhost:3000`.

## Pages by Role

### Admin
| Page | Path | Description |
|------|------|-------------|
| Overview | `/overview` | KPI cards, trend chart, bar chart, doughnut, top students |
| Sessions | `/sessions` | All sessions table with close action |
| Session Detail | `/sessions/:id` | Full attendance record list for one session |
| Attendance | `/attendance` | All records table + student rate bars |
| Courses | `/courses` | Course cards + enroll/unenroll students |
| Users | `/users` | Full user CRUD with role management |

### Teacher (same as Admin minus Users + Courses)
| Page | Path |
|------|------|
| Overview | `/overview` |
| Sessions | `/sessions` |
| Session Detail | `/sessions/:id` |
| Attendance | `/attendance` |

### Student
| Page | Path | Description |
|------|------|-------------|
| My Attendance | `/my-attendance` | Personal attendance history per course with rate bars |

## Test Credentials (after backend seed)
| Role | Email | Password |
|------|-------|----------|
| Admin | admin@tapin.app | Admin@1234 |
| Teacher | teacher@tapin.app | Teacher@1234 |
| Student | alice@tapin.app | Student@1234 |

## Tech Stack
- **React 18** + React Router v6
- **Vite** for dev server + build
- **TailwindCSS** for styling (navy + blue palette)
- **Chart.js** + react-chartjs-2 for all charts
- **Axios** with JWT interceptor + 401 auto-redirect
