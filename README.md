# Azure-POC

Hi,

The Book-Shop and Book-Supplier, in this Azure-POC respository, are 2 java projects (employing Spring MVC), developed to be deployed as Azure WebApp to show case 



	1. Use of Azure Blob Storage, where-in, uploaded documents/ pictures (instead of a BLOB in a RDBMS) can be stored.
	2. Use of Azure Service bus, to communicate between 2 applications


<b>Book Shop</b>, displays the list of books, it has in its inventory.
Some basic info such as Author, Title, Price and a Preview.
The preview, is the picture or equivalent, uploaded by the Book Supplier.
	(It is retrieved from Azure Blob Storage)
Information of each book, is communicated by the Book Supplier, as a JSON string), and employs the Azure Service bus. (which supports AMQP 1.0)
This is to simulate a J2EE application, that uses employs JMS (and a listener, of some sort - Spring MDP or Message Driven bean), to receive and process, requests, asynchronously, from multiple applications. Each request that is received, is expected to be a JSON string, containing a books Name, Author, Price and a URL. All this is parsed and inserted into a MySQL database (on Azure), employing Hibernate and then displays them.
As can be seen, Azure provides ready to use, infra-structure, so that one need not install and configure a Messaging middleware.


<b>Book Supplier</b>, allows a supplier to supply books to the Book Shop.
The supplier is provided with a UI, to key in basic information such as Author, Title and Price. The UI also gives the supplier  provision to upload a preview (image or document). In actuality, MkYong's Spring Upload project has been utilised here. The upload document is stored, as a BLOB in Azure BLOB storage. The URL of the BLOB storage and other particulars uploaded by the supplier are communicated to Book Shop, over the Azure Service bus..


thanks
