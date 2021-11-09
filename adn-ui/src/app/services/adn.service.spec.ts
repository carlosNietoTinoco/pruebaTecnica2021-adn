import { TestBed } from '@angular/core/testing';

import { AdnService } from './adn.service';

describe('AdnService', () => {
  let service: AdnService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(AdnService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
