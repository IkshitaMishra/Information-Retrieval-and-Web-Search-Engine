<?php



header('Content-Type: text/html; charset=utf-8');
$limit = 10;
$query = isset($_REQUEST['q']) ? $_REQUEST['q'] : false;

$results = false;


if ($query)
{

 
 require_once('Apache/Solr/Service.php');
 $solr = new Apache_Solr_Service('localhost', 8983,'/solr/iks');

 if (get_magic_quotes_gpc() == 1)
 {
	 $query = stripslashes($query);
 }
 try { 
		
		if($_REQUEST['opt']=='Lucene') {
			$additionalparam=array('sort' => '');
		}
		else  {
			$additionalparam=array('sort' => 'pageRankFile desc');
			
		}
		$results = $solr->search($query, 0, $limit,$additionalparam);
  } 
 
 catch (Exception $e) 
{ 
		die("<html><head><title>SEARCH EXCEPTION</title><body><pre>{$e->__toString()}</pre></body></html>");
  } 
}


$data = array_map('str_getcsv', file('URLtoHTML_nypost.csv'));

?>

<html> 

<head> 
	<title>Assignment 4 - Solr</title>
</head>
<body> 
	<form accept-charset="utf-8" method="get"> 
		<div align="center" style="font-size:25px;font-weight:bold">
		<label for="q"> Search Query :</label>
		<input id="q" name="q" type="text" value="<?php echo htmlspecialchars($query, ENT_QUOTES, 'utf-8'); ?>"/>
		</div>
		<div align="center">
			<input type="radio" name="opt" value="Lucene" id="radio1" <?php if(isset($_REQUEST['opt']) and $_REQUEST[ 'opt']=='Lucene' ) echo "checked"; ?>><label for="radio1">Lucene</label>
			<input type="radio" name="opt" value="pageRank" id="radio2" <?php if(isset($_REQUEST['opt']) and $_REQUEST[ 'opt']=='pageRank' ) echo "checked"; ?>><label for="radio2">Page Rank</label>
		</div>
		<div align="center">
			<button type="submit">Submit</button>
		</div>
	</form>



<?php 
if ($results) { 
	#Display Results

	$total = (int) $results->response->numFound; 
	$start = min(1, $total); 
	$end = min($limit, $total); 
?> 
Results : <?php echo $start; ?> -  <?php echo $end;?> out of Total Results: <?php echo $total; ?>.
<ol> 
	

<?php

  foreach ($results->response->docs as $doc)
  {
	$did = $doc->id;

  	$dtitle = $doc->title;

  	$durl = $doc->og_url;

  	$ddesc = $doc->og_description;

	if($dtitle == "" or $dtitle == null)
  	{
  		$dtitle = "N/A";
	}
  	if($ddesc == "" or $ddesc == null)
  	{
  		$ddesc = "N/A";
	}
	
	if($durl == "" or $durl == null)
	{
	   
		   $id = str_replace("/home/ikshita/Downloads/solr-7.5.0/nypost/","",$did);
		   foreach($data as $line)
		   {
		    if($id==$line[0])
		    {
		      $durl = $line[1];
		      break;
		    }
		   }
		   unset($line);
	}

	echo "<hr>";
	echo 	"1. Title : <a href = '$durl'>$dtitle</a></br>";

	echo	"2. URL : <a href = '$durl'>$durl</a></br>";

	echo	"3. ID : $did</br>";

	echo	"4. Description : $ddesc </br>";
	}
}
?>

</body> 
</html>
