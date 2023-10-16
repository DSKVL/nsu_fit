import * as React from 'react';
import Link from '@mui/material/Link';
import Table from '@mui/material/Table';
import TableBody from '@mui/material/TableBody';
import TableCell from '@mui/material/TableCell';
import TableHead from '@mui/material/TableHead';
import TableRow from '@mui/material/TableRow';
import Title from './Title';
import {useEffect, useState} from "react";
import {secureClient} from "./services/client";
import {Box, Modal} from "@mui/material";
import {Button} from "react-bootstrap";

function preventDefault(event) {
    event.preventDefault();
}

function project(o, f) {
    let projectedObj = {}
    f.forEach(k => projectedObj[k] = o[k])
    return projectedObj;
}

function RowShow({row}) {
    const rows = Object.keys(row).map((k, idx) =>
        <tr key={idx}>
            <td>{k}</td>
            <td>{row[k]}</td>
        </tr>)

    return <table className="editor-show">
        <tbody>
        {rows}
        </tbody>
    </table>
}


function RowEditForm({row, setRow}) {
    return <div>
        <form onSubmit={()=>setRow()}>
            {Object.keys(row).map((k, idx) =>
                <div key={idx} className="form-group">
                    <label>{k}</label>
                    <input className="input" type='text' name={k}
                           value={row[k]} onChange={(event) => {
                               let key = event.target.name
                               let value = event.target.value
                        setRow({...row, [key]: value})
                    }}/>
                </div>
            )}
        </form>
    </div>
}

function RowEditor({dom, row, setRow, closeModal}) {
    const [edit, setEditState] = useState(false)
    const [copy,setCopy] = useState(structuredClone(row))
    const [saving, setSaving] = useState(false)
    const style = {
        position: 'absolute',
        top: '50%',
        left: '50%',
        transform: 'translate(-50%, -50%)',
        width: 400,
        bgcolor: 'background.paper',
        border: '2px solid #000',
        boxShadow: 24,
        p: 4,
    };

    useEffect(() => { (async () => {
        const id = row.id
        Object.assign(row, copy)

        try {
            const response = await secureClient.put(dom + '/' + id,
                row, {})
                .then(response => setRow(row))
                .catch(err => console.log(err.message));
        } catch (error) {
            console.log(error.message);
        }
        setSaving(false)
    })()
    }, [saving, dom]);

    return <Box sx={style}>
        {edit ? <RowEditForm row={copy} setRow={setCopy}/> : <RowShow row={row}/> }
        <Button className="btn" onClick={() =>setEditState(!edit)}>Edit</Button>
        {edit && <Button
            disabled={saving}
            className="btn"
            onClick={()=>setSaving(true)}>Save</Button>}
        <Button className="btn" onClick={() => closeModal()}>Close</Button>
    </Box>
}

export default function DBView({fields, dom}) {
    const [currentPage, setCurrentPage] = useState(0);
    const [currentRow, setRow] = useState({});
    const [rows, setRows] = useState([]);
    const [open, setOpen] = React.useState(false);
    const [deleting, setDeleting] = useState(false)
    const [deletingId, setDeletingId] = useState(-1)
    const [deletingIdx, setDeletingIdx] = useState(-1)
    const [forbidden, setForbidden] = useState(false)

    const openView = (r) => {
        setRow(r)
        setOpen(true)
    }

    useEffect(() => { (async () => {
        try {
            const response = await secureClient.get(dom,{ params: { page: currentPage } })
                .then(response => setRows(response.data),
                        err => setForbidden(true))
        } catch (error) {
            console.log(error.message);
        }
    })()
        //TODO paging requests
    }, [currentPage, dom]);


    useEffect(() => { (async () => {
        if (!deleting) return
        try {
            console.log(dom+'/'+deletingId)
            const response = await secureClient.delete(dom+'/'+deletingId,{})
                .then(response => rows.splice(deletingIdx, 1),
                    err => console.log(err.message))
        } catch (error) {
            console.log(error.message);
        }
        setDeleting(false)
    })()}, [deleting, dom]);

    const tableHeadEntries = fields.map(field => {return <TableCell> {field} </TableCell>;})

    const style = {
        position: 'absolute',
        top: '50%',
        left: '50%',
        transform: 'translate(-50%, -50%)',
        width: 400,
        bgcolor: 'background.paper',
        border: '2px solid #000',
        boxShadow: 24,
        p: 4,
    };

    return (
        <React.Fragment>
            <Title>{dom}</Title>
            <Table size="small">
                <TableHead>
                    <TableRow>
                        {tableHeadEntries}
                    </TableRow>
                </TableHead>
                <TableBody>
                    {rows.map((obj, idx) =>
                        <TableRow key={idx} hover>
                            {Object.values(project(obj, fields)).map((val, idx) =>
                            <TableCell key={idx} >{val}</TableCell>)}
                            <TableCell>
                                <Button variant="contained" onClick={() => openView(obj)}>Edit</Button>
                            </TableCell>
                            <TableCell>
                                <Button name={idx} value={obj.id} variant="contained" onClick={() => {
                                    setDeletingId(obj.id)
                                    setDeletingIdx(idx)
                                    setDeleting(true)
                                }}>
                                    Delete</Button>
                            </TableCell>
                        </TableRow>)
                    }
                </TableBody>
            </Table>
            <Link color="primary" href="#" onClick={preventDefault} sx={{ mt: 3 }}>
                See more orders
            </Link>
            <Modal open={open} onClose={() => setOpen(false)}>
                <RowEditor
                    dom={dom}
                row={currentRow}
                setRow={setRow}
                closeModal={() => setOpen(false)}/>
            </Modal>
            <Modal open={forbidden} onClose={() => setForbidden(false)}>
                <Box sx={style}>
                    <div>
                        <Title>Forbidden</Title>
                    </div>
                </Box>
            </Modal>
        </React.Fragment>
    );
}
