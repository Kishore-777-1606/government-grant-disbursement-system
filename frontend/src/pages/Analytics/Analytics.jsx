import { useEffect, useState } from "react";

import {
  Grid,
  Typography,
  Button,
  Box,
  CircularProgress,
} from "@mui/material";

import RefreshIcon from "@mui/icons-material/Refresh";

import {
  People,
  AccountBalance,
  Assignment,
  PendingActions,
} from "@mui/icons-material";


import MainLayout from "../../layouts/MainLayout";

import AnimatedDashboardCard from "../../components/AnimatedDashboardCard";
import ChartCard from "../../components/ChartCard";

import FundBarChart from "../../components/FundBarChart";
import RegionPieChart from "../../components/RegionPieChart";
import CategoryPieChart from "../../components/CategoryPieChart";
import BudgetExhaustionChart from "../../components/BudgetExhaustionChart";
import ApprovalTurnaroundChart from "../../components/ApprovalTurnaroundChart";
import RecentActivities from "../../components/RecentActivities";


import {
  getDashboardSummary,
  getFundUtilization,
  getRegionUtilization,
  getCategoryDistribution,
  getBudgetExhaustion,
} from "../../api/analyticsApi";



function Analytics() {


const [loading,setLoading] = useState(false);



const [summary,setSummary] = useState({

 totalBeneficiaries:0,
 totalSchemes:0,
 totalApplications:0,
 pendingMilestones:0

});



const [fundData,setFundData] = useState([]);
const [regionData,setRegionData] = useState([]);
const [categoryData,setCategoryData] = useState([]);
const [budgetData,setBudgetData] = useState([]);





const loadDashboardData = async()=>{


try{


setLoading(true);



const summaryResponse =
await getDashboardSummary();

setSummary(
summaryResponse.data || {}
);



const fundResponse =
await getFundUtilization();

setFundData(
fundResponse.data || []
);



const regionResponse =
await getRegionUtilization();

setRegionData(
regionResponse.data || []
);



const categoryResponse =
await getCategoryDistribution();

setCategoryData(
categoryResponse.data || []
);



const budgetResponse =
await getBudgetExhaustion();

setBudgetData(
budgetResponse.data || []
);



}

catch(error){

console.log(
"Analytics Error:",
error
);

}


finally{

setLoading(false);

}


};





useEffect(()=>{


const fetchData = async()=>{

await loadDashboardData();

};


fetchData();


},[]);






return (

<MainLayout>


<Box

sx={{

background:"#f5f7fb",

minHeight:"100vh",

p:3

}}

>


<Typography

variant="h4"

fontWeight="bold"

mb={2}

>

Analytics Dashboard

</Typography>



<Typography mb={3}>

Monitor government grant allocation,
beneficiary progress, fund utilization
and approval workflow.

</Typography>





<Box

sx={{

display:"flex",

justifyContent:"flex-end",

mb:3

}}

>


<Button

variant="contained"

startIcon={<RefreshIcon/>}

onClick={loadDashboardData}

>

Refresh Data

</Button>


</Box>







<Grid container spacing={3}>


<Grid size={{xs:12,md:3}}>


<AnimatedDashboardCard

title="Total Beneficiaries"

value={summary.totalBeneficiaries}

icon={<People/>}

gradient="linear-gradient(135deg,#1976d2,#42a5f5)"

/>


</Grid>



<Grid size={{xs:12,md:3}}>


<AnimatedDashboardCard

title="Total Schemes"

value={summary.totalSchemes}

icon={<AccountBalance/>}

gradient="linear-gradient(135deg,#2e7d32,#66bb6a)"

/>


</Grid>




<Grid size={{xs:12,md:3}}>


<AnimatedDashboardCard

title="Total Applications"

value={summary.totalApplications}

icon={<Assignment/>}

gradient="linear-gradient(135deg,#ed6c02,#ffb74d)"

/>


</Grid>



<Grid size={{xs:12,md:3}}>


<AnimatedDashboardCard

title="Pending Milestones"

value={summary.pendingMilestones}

icon={<PendingActions/>}

gradient="linear-gradient(135deg,#9c27b0,#ce93d8)"

/>


</Grid>


</Grid>





{
loading &&

<Box

sx={{

display:"flex",

justifyContent:"center",

my:4

}}

>

<CircularProgress/>

</Box>

}





<Grid

container

spacing={3}

mt={2}

>


<Grid size={{xs:12,md:6}}>

<ChartCard title="Scheme-wise Fund Utilization">

<FundBarChart data={fundData}/>

</ChartCard>

</Grid>



<Grid size={{xs:12,md:6}}>

<ChartCard title="Region-wise Fund Utilization">

<RegionPieChart data={regionData}/>

</ChartCard>

</Grid>



<Grid size={{xs:12,md:6}}>

<ChartCard title="Category-wise Distribution">

<CategoryPieChart data={categoryData}/>

</ChartCard>

</Grid>



<Grid size={{xs:12,md:6}}>

<ChartCard title="Budget Allocation vs Disbursement">

<BudgetExhaustionChart data={budgetData}/>

</ChartCard>

</Grid>




<Grid size={{xs:12,md:6}}>

<ChartCard title="Approval Turnaround">

<ApprovalTurnaroundChart/>

</ChartCard>

</Grid>




<Grid size={{xs:12,md:6}}>

<ChartCard title="Recent Activities">

<RecentActivities/>

</ChartCard>

</Grid>



</Grid>


</Box>


</MainLayout>

);


}


export default Analytics;