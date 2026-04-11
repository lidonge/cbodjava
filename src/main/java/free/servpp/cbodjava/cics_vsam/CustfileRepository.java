package free.servpp.cbodjava.cics_vsam;

import cbod.java.cics_vsam.Custcics;
import free.cobol2java.cics.CicsCrudRepository;
import free.cobol2java.cics.CicsDataAccessException;
import free.cobol2java.cics.DuplicateKeyException;
import free.cobol2java.cics.RecordNotFoundException;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository("custfileRepository")
public class CustfileRepository implements CicsCrudRepository<Object, Object> {
    private final CustfileMapper mapper;

    public CustfileRepository(CustfileMapper mapper) {
        this.mapper = mapper;
    }

    @Override
    public void write(Object key, Object record) throws DuplicateKeyException, CicsDataAccessException {
        try {
            int updated = mapper.insert(toRow(key, record));
            if (updated <= 0) {
                throw new CicsDataAccessException("Insert CUSTFILE affected no rows.");
            }
        } catch (org.springframework.dao.DuplicateKeyException e) {
            throw new DuplicateKeyException("Duplicate key for CUSTFILE: " + key);
        } catch (CicsDataAccessException e) {
            throw e;
        } catch (Exception e) {
            throw new CicsDataAccessException("Failed to insert CUSTFILE.", e);
        }
    }

    @Override
    public Optional<Object> read(Object key) throws CicsDataAccessException {
        try {
            CustfileRow row = mapper.selectById(asKey(key));
            return row == null ? Optional.empty() : Optional.of(toCustomerRec(row));
        } catch (Exception e) {
            throw new CicsDataAccessException("Failed to read CUSTFILE.", e);
        }
    }

    @Override
    public void rewrite(Object key, Object record) throws RecordNotFoundException, CicsDataAccessException {
        try {
            int updated = mapper.update(toRow(key, record));
            if (updated <= 0) {
                throw new RecordNotFoundException("No CUSTFILE row found for key: " + key);
            }
        } catch (RecordNotFoundException e) {
            throw e;
        } catch (Exception e) {
            throw new CicsDataAccessException("Failed to update CUSTFILE.", e);
        }
    }

    @Override
    public void delete(Object key) throws RecordNotFoundException, CicsDataAccessException {
        try {
            int deleted = mapper.deleteById(asKey(key));
            if (deleted <= 0) {
                throw new RecordNotFoundException("No CUSTFILE row found for key: " + key);
            }
        } catch (RecordNotFoundException e) {
            throw e;
        } catch (Exception e) {
            throw new CicsDataAccessException("Failed to delete CUSTFILE.", e);
        }
    }

    private String asKey(Object key) {
        return key == null ? null : key.toString();
    }

    private CustfileRow toRow(Object key, Object record) {
        CustfileRow row = new CustfileRow();
        String customerId = asKey(key);
        if (record instanceof Custcics.CustomerRec customerRec) {
            row.setCustomerId(customerRec.customerId != null ? customerRec.customerId : customerId);
            row.setCustomerName(customerRec.customerName);
            row.setCustomerPhone(customerRec.customerPhone);
            row.setCustomerAddress(customerRec.customerAddress);
            return row;
        }
        throw new CicsDataAccessException("Unsupported CUSTFILE record type: "
                + (record == null ? "null" : record.getClass().getName()));
    }

    private Custcics.CustomerRec toCustomerRec(CustfileRow row) {
        Custcics.CustomerRec customerRec = new Custcics.CustomerRec();
        customerRec.customerId = row.getCustomerId();
        customerRec.customerName = row.getCustomerName();
        customerRec.customerPhone = row.getCustomerPhone();
        customerRec.customerAddress = row.getCustomerAddress();
        return customerRec;
    }
}
