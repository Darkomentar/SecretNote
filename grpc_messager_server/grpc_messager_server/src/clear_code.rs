use std::thread;
use std::time::Duration;
use tokio_postgres::{NoTls, Error};
extern crate postgres;
extern crate tokio_postgres;

#[tokio::main]
async fn main() {
 loop
{ 
    tokio::spawn(async move {
    let con_string = "postgresql://postgres:zero@localhost:5432/secret_note_db";
    let mut client222 = connect().await;
    let client = client222.unwrap();
    let mut q2 = client.query("call clear_reg_check_5_min()",  &[]).await;
    });
    println!("Clear confirm code stack");
    thread::sleep(Duration::from_secs(1 * 60));
}
}


async fn connect() -> Result<tokio_postgres::Client, Error> {
    let con_string = "postgresql://postgres:zero@localhost:5432/secret_note_db";

    let (client, connection) = tokio_postgres::connect(con_string, NoTls).await?;

    tokio::spawn(async move {
        if let Err(e) = connection.await {
            eprintln!("connection error: {}", e);
        }
    });

    Ok(client)
}

