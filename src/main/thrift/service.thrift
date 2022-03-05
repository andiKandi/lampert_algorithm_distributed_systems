namespace cpp de.hda.fbi.ds.mbredel.exam.thrift 
namespace java de.hda.fbi.ds.mbredel.exam.thrift
namespace py de.hda.fbi.ds.mbredel.exam.thrift
namespace go de.hda.fbi.ds.mbredel.exam.thrift

/*
 * Exception if something goes wrong.
 */
exception InvalidOperationException {
    1: i32 code,
    2: string description
}

/*
 * The resource that is stored at the
 * server.
 */
struct Resource {
    1: optional i32 id,
    2: string name,
    3: i32 size,
    4: optional string description 
}

/*
 * The service description for Thrift.
 */
service Service {

     /*
     * Stores a resource.
     */
    void storeData(1:Resource resource) throws (1:InvalidOperationException e),

    /*
     * Returns a single resource.
     */
    Resource getData(1:i32 id) throws (1:InvalidOperationException e),

    /*
     * Returns all resources.
     */
    list <Resource> listData() throws (1:InvalidOperationException e),

    /*
     * Returns health check information.
     */
    bool healthCheck() throws (1:InvalidOperationException e)
}
