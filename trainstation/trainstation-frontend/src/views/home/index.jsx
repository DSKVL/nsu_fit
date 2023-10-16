import { Button, AppBar, Card, CardContent, CircularProgress,
    Container, Grid, IconButton, Toolbar, Typography,
    Box, Alert, AlertTitle } from "@mui/material";
import { useSnackbar } from "notistack";
import { useEffect, useState } from "react";
import { getUserInfo } from "../../services/user.service";
import { Person, Menu as MenuIcon } from "@mui/icons-material";
import authService from "../../services/auth.service";
import { useNavigate } from "react-router-dom";

function HomeView() {
    const { enqueueSnackbar } = useSnackbar();
    const history = useNavigate();

    const [user, setUser] = useState({});
    const [loading, setLoading] = useState(false);

    useEffect(() => {
        setLoading(true);
        getUserInfo()
            .then(user => setUser(user))
            .catch(e => enqueueSnackbar(e, { variant: "error" }))
            .finally(() => setLoading(false));
    }, []);

    const logout = () => {
        authService.logout();
        history("/");
    }

    return (
        <Container maxWidth={false} disableGutters>
            <AppBar position="static">
                <Toolbar>
                    <IconButton edge="start"  color="inherit" aria-label="menu">
                        <MenuIcon />
                    </IconButton>
                    <Typography variant="h6">
                        Demo
                    </Typography>
                    <Button color="inherit" onClick={logout}>Logout</Button>
                </Toolbar>
            </AppBar>
            <Grid container
                  justifyContent={"center"}
                  alignContent={"center"}
                  spacing={3}
            >
                <Grid item xs={6}>
                    <Card>
                        <CardContent>
                            {loading && (
                                <CircularProgress />
                            )}

                            {!!user && (
                                <Box
                                    display="flex"
                                    alignItems={"center"}
                                    flexDirection="column"
                                >
                                    <Person />
                                    <Box width={0.5}>
                                        <Alert>
                                            <AlertTitle>Hello {user.username}!</AlertTitle>
                                            Welcome back!
                                        </Alert>
                                    </Box>
                                </Box>
                            )}
                        </CardContent>
                    </Card>
                </Grid>
            </Grid>
        </Container>
    );
}

export default HomeView;