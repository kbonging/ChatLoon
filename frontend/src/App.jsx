import './App.css'
import AppRouter from './routes/AppRouter'
import { AppProvider } from './contexts/AppContext'
import { BrowserRouter } from "react-router-dom";

function App() {

  return (
    <>
      <BrowserRouter>
        <AppProvider>
          <AppRouter/>
        </AppProvider>
      </BrowserRouter>
    </>
  )
}

export default App
