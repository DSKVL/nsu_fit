import { Button, Card, CardContent, CardHeader, Container, Grid, TextField } from "@mui/material";
import { useSnackbar } from "notistack";
import { useState } from "react";
import { useNavigate } from "react-router-dom";
import { login, setToken } from "../../services/auth.service";

export default function LoginView(props) {
    const history = useNavigate();
    const { enqueueSnackbar } = useSnackbar();

    const [username, setUsername] = useState();
    const [password, setPassword] = useState();

    const handleLogin = (e) => {
        e.preventDefault();
        login(username, password)
            .then(response => {
                setToken(response.jwt);
                enqueueSnackbar("Login successful!", { variant: "success" });
                history("/dashboard/workers");
            })
            .catch(e => {
                enqueueSnackbar("Login failed!", { variant: "error" });
            });
    };

    return (
        <Container maxWidth={false}>
            <Container maxWidth={"sm"}>
                <Card>
                    <CardHeader title={"Login"} />
                    <CardContent>
                        <form onSubmit={handleLogin}>
                            <Grid container
                                  display={"flex"}
                                  direction={"column"}
                                  spacing={2}
                            >
                                <Grid item>
                                    <TextField
                                        variant="outlined"
                                        placeholder={"Username"}
                                        value={username ?? ""}
                                        onChange={e => setUsername(e.target.value)}
                                        fullWidth
                                    />
                                </Grid>
                                <Grid item>
                                    <TextField
                                        variant="outlined"
                                        placeholder={"Password"}
                                        type={"password"}
                                        value={password ?? ""}
                                        onChange={e => setPassword(e.target.value)}
                                        fullWidth
                                    />
                                </Grid>
                                <Grid item>
                                    <Button
                                        variant={"contained"}
                                        color={"primary"}
                                        type="submit"
                                    >
                                        Login
                                    </Button>
                                </Grid>
                            </Grid>
                        </form>
                    </CardContent>
                </Card>
            </Container>
        </Container>
    );
}