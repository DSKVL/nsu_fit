import { render, screen } from '@testing-library/react';
import WorkersPage from './WorkersPage';

test('renders learn react link', () => {
  render(<WorkersPage />);
  const linkElement = screen.getByText(/learn react/i);
  expect(linkElement).toBeInTheDocument();
});
