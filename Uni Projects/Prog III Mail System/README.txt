This was one of the most entertaining uni project of the three-year degree course (unfortunately, it was worth
only three credits). It consisted of a complete client/server mail system, with the server which listens on
a door, and when a client arrives, the mail server assigns it a handler (a thread of its threadpool), which
will manage its requestes.
This project puts it together FXML, java threads, java sockets, java connection r/w to a file (syncronized by
the read/write lock tool), observer/observable ListViews and much more.
This project is built following the MVC pattern (Model, View and Controller).