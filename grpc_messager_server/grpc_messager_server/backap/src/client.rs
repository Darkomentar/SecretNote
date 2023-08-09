use testproto::nuzhniitest_client::NuzhniitestClient;
use testproto::NuzhniiRequest;

pub mod testproto {
    tonic::include_proto!("testproto");
}

#[tokio::main]
async fn main() -> Result<(), Box<dyn std::error::Error>> {
    let mut client = NuzhniitestClient::connect("http://[::1]:50051").await?;

    let request = tonic::Request::new(
        NuzhniiRequest {
            message: "Timur".to_owned(),
            number: "222".to_owned(),
        }
    );

    let response = client.superfunc(request).await?;
   
    println!("сообщение от сервера: {:?}",  response.get_ref().message);

    Ok(())
}