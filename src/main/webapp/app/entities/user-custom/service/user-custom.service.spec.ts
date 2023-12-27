import { TestBed } from '@angular/core/testing';
import { HttpClientTestingModule, HttpTestingController } from '@angular/common/http/testing';

import { DATE_FORMAT } from 'app/config/input.constants';
import { IUserCustom } from '../user-custom.model';
import { sampleWithRequiredData, sampleWithNewData, sampleWithPartialData, sampleWithFullData } from '../user-custom.test-samples';

import { UserCustomService, RestUserCustom } from './user-custom.service';

const requireRestSample: RestUserCustom = {
  ...sampleWithRequiredData,
  birthDay: sampleWithRequiredData.birthDay?.format(DATE_FORMAT),
  creationDate: sampleWithRequiredData.creationDate?.toJSON(),
  modificationDate: sampleWithRequiredData.modificationDate?.toJSON(),
  deletionDate: sampleWithRequiredData.deletionDate?.toJSON(),
};

describe('UserCustom Service', () => {
  let service: UserCustomService;
  let httpMock: HttpTestingController;
  let expectedResult: IUserCustom | IUserCustom[] | boolean | null;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule],
    });
    expectedResult = null;
    service = TestBed.inject(UserCustomService);
    httpMock = TestBed.inject(HttpTestingController);
  });

  describe('Service methods', () => {
    it('should find an element', () => {
      const returnedFromService = { ...requireRestSample };
      const expected = { ...sampleWithRequiredData };

      service.find(123).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'GET' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should create a UserCustom', () => {
      const userCustom = { ...sampleWithNewData };
      const returnedFromService = { ...requireRestSample };
      const expected = { ...sampleWithRequiredData };

      service.create(userCustom).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'POST' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should update a UserCustom', () => {
      const userCustom = { ...sampleWithRequiredData };
      const returnedFromService = { ...requireRestSample };
      const expected = { ...sampleWithRequiredData };

      service.update(userCustom).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'PUT' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should partial update a UserCustom', () => {
      const patchObject = { ...sampleWithPartialData };
      const returnedFromService = { ...requireRestSample };
      const expected = { ...sampleWithRequiredData };

      service.partialUpdate(patchObject).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'PATCH' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should return a list of UserCustom', () => {
      const returnedFromService = { ...requireRestSample };

      const expected = { ...sampleWithRequiredData };

      service.query().subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'GET' });
      req.flush([returnedFromService]);
      httpMock.verify();
      expect(expectedResult).toMatchObject([expected]);
    });

    it('should delete a UserCustom', () => {
      const expected = true;

      service.delete(123).subscribe(resp => (expectedResult = resp.ok));

      const req = httpMock.expectOne({ method: 'DELETE' });
      req.flush({ status: 200 });
      expect(expectedResult).toBe(expected);
    });

    it('should handle exceptions for searching a UserCustom', () => {
      const queryObject: any = {
        page: 0,
        size: 20,
        query: '',
        sort: [],
      };
      service.search(queryObject).subscribe(() => expectedResult);

      const req = httpMock.expectOne({ method: 'GET' });
      req.flush(null, { status: 500, statusText: 'Internal Server Error' });
      expect(expectedResult).toBe(null);
    });

    describe('addUserCustomToCollectionIfMissing', () => {
      it('should add a UserCustom to an empty array', () => {
        const userCustom: IUserCustom = sampleWithRequiredData;
        expectedResult = service.addUserCustomToCollectionIfMissing([], userCustom);
        expect(expectedResult).toHaveLength(1);
        expect(expectedResult).toContain(userCustom);
      });

      it('should not add a UserCustom to an array that contains it', () => {
        const userCustom: IUserCustom = sampleWithRequiredData;
        const userCustomCollection: IUserCustom[] = [
          {
            ...userCustom,
          },
          sampleWithPartialData,
        ];
        expectedResult = service.addUserCustomToCollectionIfMissing(userCustomCollection, userCustom);
        expect(expectedResult).toHaveLength(2);
      });

      it("should add a UserCustom to an array that doesn't contain it", () => {
        const userCustom: IUserCustom = sampleWithRequiredData;
        const userCustomCollection: IUserCustom[] = [sampleWithPartialData];
        expectedResult = service.addUserCustomToCollectionIfMissing(userCustomCollection, userCustom);
        expect(expectedResult).toHaveLength(2);
        expect(expectedResult).toContain(userCustom);
      });

      it('should add only unique UserCustom to an array', () => {
        const userCustomArray: IUserCustom[] = [sampleWithRequiredData, sampleWithPartialData, sampleWithFullData];
        const userCustomCollection: IUserCustom[] = [sampleWithRequiredData];
        expectedResult = service.addUserCustomToCollectionIfMissing(userCustomCollection, ...userCustomArray);
        expect(expectedResult).toHaveLength(3);
      });

      it('should accept varargs', () => {
        const userCustom: IUserCustom = sampleWithRequiredData;
        const userCustom2: IUserCustom = sampleWithPartialData;
        expectedResult = service.addUserCustomToCollectionIfMissing([], userCustom, userCustom2);
        expect(expectedResult).toHaveLength(2);
        expect(expectedResult).toContain(userCustom);
        expect(expectedResult).toContain(userCustom2);
      });

      it('should accept null and undefined values', () => {
        const userCustom: IUserCustom = sampleWithRequiredData;
        expectedResult = service.addUserCustomToCollectionIfMissing([], null, userCustom, undefined);
        expect(expectedResult).toHaveLength(1);
        expect(expectedResult).toContain(userCustom);
      });

      it('should return initial array if no UserCustom is added', () => {
        const userCustomCollection: IUserCustom[] = [sampleWithRequiredData];
        expectedResult = service.addUserCustomToCollectionIfMissing(userCustomCollection, undefined, null);
        expect(expectedResult).toEqual(userCustomCollection);
      });
    });

    describe('compareUserCustom', () => {
      it('Should return true if both entities are null', () => {
        const entity1 = null;
        const entity2 = null;

        const compareResult = service.compareUserCustom(entity1, entity2);

        expect(compareResult).toEqual(true);
      });

      it('Should return false if one entity is null', () => {
        const entity1 = { id: 123 };
        const entity2 = null;

        const compareResult1 = service.compareUserCustom(entity1, entity2);
        const compareResult2 = service.compareUserCustom(entity2, entity1);

        expect(compareResult1).toEqual(false);
        expect(compareResult2).toEqual(false);
      });

      it('Should return false if primaryKey differs', () => {
        const entity1 = { id: 123 };
        const entity2 = { id: 456 };

        const compareResult1 = service.compareUserCustom(entity1, entity2);
        const compareResult2 = service.compareUserCustom(entity2, entity1);

        expect(compareResult1).toEqual(false);
        expect(compareResult2).toEqual(false);
      });

      it('Should return false if primaryKey matches', () => {
        const entity1 = { id: 123 };
        const entity2 = { id: 123 };

        const compareResult1 = service.compareUserCustom(entity1, entity2);
        const compareResult2 = service.compareUserCustom(entity2, entity1);

        expect(compareResult1).toEqual(true);
        expect(compareResult2).toEqual(true);
      });
    });
  });

  afterEach(() => {
    httpMock.verify();
  });
});
