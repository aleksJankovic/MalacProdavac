import pana from "../assets/pana.png"

function DownloadButton(){

    const handleDownload = () => {
        const apkFilePath = "../";
    
        const downloadLink = document.createElement('a');
        downloadLink.href = apkFilePath;
        downloadLink.download = "MalacProdavacBatmobile.apk";
    
        document.body.appendChild(downloadLink);
        downloadLink.click();
    
        document.body.removeChild(downloadLink);
      };
    

    return(
        <button className="download-place-button button-effect" onClick={handleDownload}>Preuzmi aplikaciju</button>
    )
}

function DownloadPlace(){
    return(
        <div className="download-place-conatainer">
            <div className="download-place-information">
                <h1>Malac Prodavac</h1>
                <p style={{fontStyle:"italic"}} >Mali proizvođači, veliki izbor!</p>
            </div>
            <DownloadButton />
            <p style={{marginTop:"30px"}}>Aplikacija razvojnog tima: <span style={{fontWeight:"bold"}}>Batmobile</span></p>
        </div>
    )
}

function Home(){
    return(
        <div className="home-container">
            <div className="home-img">
                <img src={pana} alt="pana" />
            </div>
            <DownloadPlace />
        </div>
    )
}

export default Home;
