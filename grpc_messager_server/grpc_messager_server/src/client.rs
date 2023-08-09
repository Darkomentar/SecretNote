// use testproto::ChatListSRequest;
// use testproto::nuzhniitest_client::NuzhniitestClient;
// use testproto::NuzhniiRequest;
// use testproto::LoginRequest;
// use testproto::EmailConfirmationRequest;
// use testproto::RegistrationRequest;
// use testproto::SendMsgRequest;
// use testproto::GetMsgRequest;
// use testproto::GetMsgStreamRequest;
// use testproto::GetLoginsRequest;
// use std::fs::File;
// use std::io::BufWriter;

// use image::jpeg::JPEGEncoder;
// use image::ColorType;
// use image::ImageBuffer;

// pub mod testproto {
//     tonic::include_proto!("testproto");
// }

// #[tokio::main]
// async fn main() -> Result<(), Box<dyn std::error::Error>> {


//     // let mut client = NuzhniitestClient::connect("http://94.41.19.40:50030").await?;
//     // let request = tonic::Request::new(
//     //     NuzhniiRequest {
//     //         message: "Timur".to_owned(),
//     //         number: "222".to_owned(),
//     //     }
//     // );
//     // let response = client.superfunc(request).await?;
//     // println!("сообщение от сервера: {:?}",  response.get_ref().message);
//     // Ok(())


//     // login


//     // let mut client = NuzhniitestClient::connect("http://94.41.19.40:50030").await?;
//     // let request = tonic::Request::new(
//     //     LoginRequest {
//     //         login: "Login".to_owned(),
//     //         password: "Password".to_owned(),
//     //     }
//     // );
//     // let response = client.login(request).await?;
//     // println!("сообщение от сервера: {} {} {} {} {} {} {}",response.get_ref().entered, response.get_ref().email, response.get_ref().phone, response.get_ref().info, response.get_ref().surname, response.get_ref().name, response.get_ref().middle_name);
//     // Ok(())


//     // Email_confirm

//     // let mut client = NuzhniitestClient::connect("http://94.41.19.40:50030").await?;
//     // let request = tonic::Request::new(
//     //     EmailConfirmationRequest {
//     //         email: "danilnasretdinov2002@gmail.com".to_owned(),
//     //     }
//     // );
//     // let response = client.email_confirmation(request).await?;
//     // println!("сообщение от сервера: {} {} ",response.get_ref().success, response.get_ref().errorcode);
//     // Ok(())

    
   
//     // Registration

//     // let mut client = NuzhniitestClient::connect("http://94.41.19.40:50030").await?;
//     // let request = tonic::Request::new(
//     //     RegistrationRequest {
//     //         surname: "Насретдинов".to_owned(),
//     //         name: "Данил".to_owned(),
//     //         middle_name: "Насретдинов".to_owned(),
//     //         password: "zero".to_owned(),
//     //         login: "danil228".to_owned(),
//     //         email: "danilnasretdinov2002@gmail.com".to_owned(),
//     //         confirmcode: 502370.to_owned(),
//     //         publickey: "MIICIjANBgkqhkiG9w0BAQEFAAOCAg8AMIICCgKCAgEAtguKxAjjH6uzAis1/CTXWfxWfWxIzTNecJIU3vZ6szSlbl369RTJrfehLUulbLto+OHLDmf3L7hj313TIuIAnT6y/ZkBY+0FFSdEh5adVrP0U1g1l10PVQDFquEJnafYW4sEmX57EjyownYXMYEE0mi5hXe2yjUxL7gC+vRiVUHqS/ovxc/Ea2qksReIFp6HxCsfAlYAxU0ci8O4cXOJjwa8i79EYXekJn+xzMV67XvrpEUXnBHEpu6c7NEXK+0T73/CtoAveF1ICDjV/LIDXDLXlIbfG+Vr6v8sPzjgd9HG7ttsb73dUxwpf1pfl/eHpD01l8xHI3mGmSgTOk+F2zYBhyYDKwVoXZ2DxMOsrAUgR9MgkXUzG4FGkTtcfOFl/MuIvTZG0oGyJvICDdJxBnral9DlnC3b5wRk1ul3s72vuLNKiErBCmpHZFVktpQ+0xsClhBXP75mrYLdEXNmjPgoCHe5iS6gf6Fk1QGwHmczbAjJxSaPx/2uW2a3yJPJL2AtweWGe2eu1MDyXlBibAaXrSywq/gUa+D4suj3bvIXHj/ZUF1S1KrRAjC38n7LtSlskT8UyapInCRjB2dAr0bf72gtnfi1CTzFC+yn5yjeQXwj/8YzEEYgzaR+YsRsSGGy8hS1TxgoyCRBYwRjgYIh6ah+yl2Y/ABKFuQHUCAwEAAQ==".to_owned()
//     //     }
//     // );
//     // let response = client.registration(request).await?;
//     // println!("сообщение от сервера: {} ",response.get_ref().errorcode);
//     // Ok(())


//     // send_message

//     // let mut ss = "Увлекая амые речевыеку, фольклор.";
//     // let mut client = NuzhniitestClient::connect("http://94.41.19.40:50030").await?;
//     // let request = tonic::Request::new(
//     //     SendMsgRequest {
//     //         login : "Login".to_owned(),
//     //         password  : "Password".to_owned(),
//     //         recipientid  : 1.to_owned(),
//     //         sender_msg  : ss.as_bytes().to_owned(),
//     //         recipient_msg : ss.as_bytes().to_owned(),
//     //         typemsg : "text".to_owned()
           
//     //     }
//     // );
//     // let response = client.send_msg(request).await?;
//     // println!("сообщение от сервера: {} ",response.get_ref().errorcode);
//     // Ok(())

//         // get_message

//     // let mut ss = "Увлекая амые речевыеку, фольклор.";
//     // let mut client = NuzhniitestClient::connect("http://94.41.19.40:50030").await?;
//     // let request = tonic::Request::new(
//     //     GetMsgRequest {
//     //         login : "Login".to_owned(),
//     //         password  : "Password".to_owned(),
//     //         recipient_login  : "Login".to_owned(),
//     //     }
//     // );
//     // let response = client.get_msg(request).await?;
//     // println!("сообщение от сервера: {}  {}  {} ",response.get_ref().errorcode,  String::from_utf8(response.get_ref().geter_msg.to_vec()).unwrap(), response.get_ref().types);
//     // Ok(())

//          // get_message

//         //  let mut ss = "Увлекая амые речевыеку, фольклор.";
//         //  let mut client = NuzhniitestClient::connect("http://94.41.19.40:50030").await?;
//         //  let request = tonic::Request::new(
//         //      GetMsgStreamRequest {
//         //          login : "Login".to_owned(),
//         //          password  : "Password".to_owned(),
//         //          recipient_login  : "Login".to_owned(),
//         //      }
//         //  );
//         //  let mut responsee = client.get_msg_s(request).await?.into_inner();
//         //  while let Some(response) = responsee.message().await? {
//         //     println!("сообщение от сервера: {}  {}  {}", response.errorcode, String::from_utf8(response.geter_msg.to_vec()).unwrap(), response.types);
          
//         // }
//         //  //println!("сообщение от сервера: {}  {}  {} ",response.get_ref().errorcode,  String::from_utf8(response.get_ref().geter_msg.to_vec()).unwrap(), response.get_ref().types);
//         //  Ok(())



//          // get_logins

  
//         //  let mut client = NuzhniitestClient::connect("http://94.41.19.40:50030").await?;
//         //  let request = tonic::Request::new(
//         //      GetLoginsRequest {
//         //          login : "i".to_owned(),
//         //      }
//         //  );
//         //  let mut responsee = client.get_logins(request).await?.into_inner();
//         //  while let Some(response) = responsee.message().await? {
//         //     println!("сообщение от сервера: {}  {} ", response.errorcode, response.login);
          
//         // }
//         //  //println!("сообщение от сервера: {}  {}  {} ",response.get_ref().errorcode,  String::from_utf8(response.get_ref().geter_msg.to_vec()).unwrap(), response.get_ref().types);
//         //  Ok(())



//         // get_last_readed

  
//              let mut ss = "Увлекая амые речевыеку, фольклор.";
//          let mut client = NuzhniitestClient::connect("http://94.41.19.40:50030").await?;
//          let request = tonic::Request::new(
//              ChatListSRequest {
//                  login : "Timur2000".to_owned(),
//                  password  : "2000Timur".to_owned(),
//              }
//          );
//          let mut responsee = client.chat_list_s(request).await?.into_inner();
//          while let Some(response) = responsee.message().await? {
//             println!("сообщение от сервера: {} {}  ", response.errorcode, response.firstname);
//           let bytes: Vec<u8> = response.photo;
//           println!("{}  ", &bytes);
//         }
//          //println!("сообщение от сервера: {}  {}  {} ",response.get_ref().errorcode,  String::from_utf8(response.get_ref().geter_msg.to_vec()).unwrap(), response.get_ref().types);
//          Ok(())



// }


// // fn save_jpeg_from_bytes(bytes: Vec<u8>, width: u32, height: u32, output_path: &str) -> Result<(), Box<dyn std::error::Error>> {
// //     let img = ImageBuffer::from_vec(width, height, bytes).ok_or("failed to create ImageBuffer")?;

// //     let file = File::create(output_path)?;
// //     let mut writer = BufWriter::new(file);

// //     let mut encoder = JPEGEncoder::new_with_quality(&mut writer, 75);
// //     encoder.encode(&img, width, height, ColorType::Rgb8)?;

// //     Ok(())
// // }
// //#[tonic::async_trait]
// //  fn login()
// // {
// //     let mut client = NuzhniitestClient::connect("http://26.84.142.201:50030").await?;

// //     let request = tonic::Request::new(
// //         LoginRequest {
// //             login: "danil1337".to_owned(),
// //             password: "danil2002".to_owned(),
// //         }
// //     );

// //     let response = client.login(request).await?;
   
// //     println!("сообщение от сервера: {:?}",  response.get_ref().entered);
// //     Ok(())
// // }

// // #[tonic::async_trait]
// // acync fn testttt()
// // {
// //     let mut client = NuzhniitestClient::connect("http://127.0.0.1:50030").await?;

// //     let request = tonic::Request::new(
// //         NuzhniiRequest {
// //             message: "Timur".to_owned(),
// //             number: "222".to_owned(),
// //         }
// //     );

// //     let response = client.superfunc(request).await?;
   
// //     println!("сообщение от сервера: {:?}",  response.get_ref().message);
// //     Ok(())
// // }