# Backend Spring Boot Project

This folder is the standalone Spring Boot backend repository for the assignment submission and grading system.

## Run locally

1. Ensure MySQL is running.
2. Set environment variables if you want values other than the defaults in `application.properties`.
3. Run:

```bash
mvn spring-boot:run
```

Default backend URL: `http://localhost:3002`

## Main API routes

- `POST /api/auth/teacher/login`
- `POST /api/auth/student/login`
- `POST /api/students/register`
- `GET /api/dashboard/teacher`
- `GET /api/dashboard/student/{studentId}`
- `POST /api/tasks`
- `POST /api/submissions`
- `PATCH /api/submissions/{submissionId}/grade`
- `GET /api/health`
