use tonic::{transport::Server, Request, Response, Status};

use testproto::nuzhniitest_server::{Nuzhniitest, NuzhniitestServer};
use testproto::{NuzhniiResponse, NuzhniiRequest};

pub mod testproto {
    tonic::include_proto!("testproto");
}

#[derive(Debug, Default)]
pub struct NuzhniitestService {}

#[tonic::async_trait]
impl Nuzhniitest for NuzhniitestService {
    async fn superfunc(
        &self,
        request: Request<NuzhniiRequest>,
    ) -> Result<Response<NuzhniiResponse>, Status> {
        println!("Вот такой запрос пришел: {:?}", request);
      //  println!("ggg: {:?}", request);
        let req = request.into_inner();

        let reply = NuzhniiResponse {
            message: format!("Здарова  {} твой номер: {}.", req.message, req.number).into(),
        };

        Ok(Response::new(reply))
    }
}

#[tokio::main]
async fn main() -> Result<(), Box<dyn std::error::Error>> {
    let addr = "26.231.228.199:50051".parse()?;
    let nuzhnii_service = NuzhniitestService::default();

    Server::builder()
        .add_service(NuzhniitestServer::new(nuzhnii_service))
        .serve(addr)
        .await?;

    Ok(())
}