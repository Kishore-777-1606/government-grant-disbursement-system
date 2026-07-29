import { useEffect, useState } from "react";
import {
    Typography,
    Paper,
    Table,
    TableBody,
    TableCell,
    TableContainer,
    TableHead,
    TableRow,
    CircularProgress,
    Button
} from "@mui/material";

import AddIcon from "@mui/icons-material/Add";

import MainLayout from "../../layouts/MainLayout";
import { getAllBeneficiaries } from "../../services/beneficiaryService";
import AddBeneficiaryDialog from "../../components/beneficiaries/AddBeneficiaryDialog";

function Beneficiaries() {

    const [beneficiaries, setBeneficiaries] = useState([]);
    const [loading, setLoading] = useState(true);

    const [openDialog, setOpenDialog] = useState(false);

    useEffect(() => {
        loadBeneficiaries();
    }, []);

    const loadBeneficiaries = async () => {

        try {

            const data = await getAllBeneficiaries();
            setBeneficiaries(data);

        } catch (err) {

            console.error(err);

        } finally {

            setLoading(false);

        }

    };

    return (

        <MainLayout>

            <div
                style={{
                    display: "flex",
                    justifyContent: "space-between",
                    alignItems: "center",
                    marginBottom: 20
                }}
            >

                <Typography variant="h4">
                    Beneficiaries
                </Typography>

                <Button
                    variant="contained"
                    startIcon={<AddIcon />}
                    onClick={() => setOpenDialog(true)}
                >
                    Add Beneficiary
                </Button>

            </div>

            {loading ? (

                <CircularProgress />

            ) : (

                <TableContainer component={Paper}>

                    <Table>

                        <TableHead>

                            <TableRow>

                                <TableCell>ID</TableCell>
                                <TableCell>Name</TableCell>
                                <TableCell>Mobile</TableCell>
                                <TableCell>Email</TableCell>

                            </TableRow>

                        </TableHead>

                        <TableBody>

                            {beneficiaries.map((b) => (

                                <TableRow key={b.id}>

                                    <TableCell>{b.id}</TableCell>

                                    <TableCell>
                                        {b.firstName} {b.lastName}
                                    </TableCell>

                                    <TableCell>{b.mobileNumber}</TableCell>

                                    <TableCell>{b.email}</TableCell>

                                </TableRow>

                            ))}

                        </TableBody>

                    </Table>

                </TableContainer>

            )}

            <AddBeneficiaryDialog
                open={openDialog}
                handleClose={() => setOpenDialog(false)}
                refreshData={loadBeneficiaries}
            />

        </MainLayout>

    );

}

export default Beneficiaries;