import * as React from 'react';
import {BrowserRouter as Router, Route, Routes} from "react-router-dom";
import SignIn from "./SignIn";
import Dashboard from "./Dashboard";
import SignUp from "./SignUp";
import { Navigate } from "react-router-dom";

export default function App() {
    return <Router>
        <Routes>
            <Route path='/' element={
                <Navigate to='/login'/>}/>
            <Route path='/dashboard/*' element={<Dashboard/>}/>
            <Route path='/login' element={<SignIn/>}/>
            <Route path='/signup' element={<SignUp/>}/>
        </Routes>
    </Router>;
}