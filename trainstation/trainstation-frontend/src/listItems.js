import * as React from 'react';
import ListItemButton from '@mui/material/ListItemButton';
import ListItemText from '@mui/material/ListItemText';
import {Link} from "react-router-dom";

export const mainListItems = (
    <React.Fragment>
        <ListItemButton component={Link} to="workers">
            <ListItemText primary="Workers" />
        </ListItemButton>
        <ListItemButton component={Link} to="brigades">
            <ListItemText primary="Brigades" />
        </ListItemButton>
        <ListItemButton component={Link} to="departments">
            <ListItemText primary="Departments" />
        </ListItemButton>
        <ListItemButton component={Link} to="drivers">
            <ListItemText primary="Drivers" />
        </ListItemButton>
        <ListItemButton component={Link} to="flights">
            <ListItemText primary="Flights" />
        </ListItemButton>
        <ListItemButton component={Link} to="delays">
            <ListItemText primary="Delays" />
        </ListItemButton>
    </React.Fragment>
);