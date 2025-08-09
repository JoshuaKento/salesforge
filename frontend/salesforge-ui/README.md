# SalesForge UI

Modern React TypeScript frontend for the SalesForge CRM application.

## Features

- **Authentication**: JWT-based login with role-based access control
- **Lead Management**: Full CRUD operations with advanced filtering and search
- **Responsive Design**: Mobile-first design that works on all devices
- **Modern UI**: Clean, professional interface with smooth animations
- **Real-time Filtering**: Instant search and filter results
- **Pagination**: Efficient data loading with pagination support

## Getting Started

### Prerequisites

- Node.js 16+ and npm
- SalesForge backend running on port 8080

### Development

```bash
# Install dependencies
npm install

# Start development server
npm start

# Open http://localhost:3000
```

### Production Build

```bash
# Build for production
npm run build

# Serve production build locally
npm run serve
```

### Environment Configuration

The app supports different API endpoints for development and production:

- **Development**: `http://localhost:8080/api/v1`
- **Production**: `https://api.salesforge.com/api/v1`

## Demo Credentials

- **Email**: demo@salesforge.com
- **Password**: demo123456

## Project Structure

```
src/
├── components/           # React components
│   ├── LoginForm.tsx    # Authentication form
│   ├── LeadsTable.tsx   # Lead management interface
│   └── LeadForm.tsx     # Lead create/edit modal
├── services/            # API services
│   └── api.ts          # Axios-based API client
└── styles/             # Component-specific CSS
```

## API Integration

The frontend integrates with the SalesForge REST API providing:

- User authentication and session management
- Lead CRUD operations with filtering and search
- Role-based access control (ADMIN, MANAGER, SALES_REP)
- Error handling and loading states

## Technologies

- **React 18** with TypeScript
- **Axios** for HTTP requests
- **CSS3** with modern styling (Flexbox, Grid)
- **Responsive Design** principles
- **LocalStorage** for token persistence

---

*This project was bootstrapped with [Create React App](https://github.com/facebook/create-react-app).*
